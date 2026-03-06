package com.orthopedic.api.modules.appointment.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.dto.request.AppointmentFilterRequest;
import com.orthopedic.api.modules.appointment.dto.request.BookAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.request.RescheduleAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.mapper.AppointmentMapper;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.entity.Hospital;
import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import com.orthopedic.api.modules.hospital.repository.ServiceRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import com.orthopedic.api.shared.exception.SlotUnavailableException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentStatsResponse;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentMapper appointmentMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final io.micrometer.core.instrument.Counter appointmentBookedCounter;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            ServiceRepository serviceRepository,
            AppointmentMapper appointmentMapper,
            RedisTemplate<String, Object> redisTemplate,
            io.micrometer.core.instrument.Counter appointmentBookedCounter) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentMapper = appointmentMapper;
        this.redisTemplate = redisTemplate;
        this.appointmentBookedCounter = appointmentBookedCounter;
    }

    private static final String LOCK_PREFIX = "lock:appointment:";

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "BOOK_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse bookAppointment(BookAppointmentRequest request, User currentUser) {
        String lockKey = LOCK_PREFIX + request.getDoctorId() + ":" + request.getAppointmentDate() + ":"
                + request.getStartTime();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(locked)) {
            throw new SlotUnavailableException(
                    "This time slot is currently being booked by another user. Please try again in 30 seconds.");
        }

        try {
            // 1. Validation
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

            ServiceEntity service = serviceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

            // Ensure service belongs to doctor's hospital or same hospital mentioned
            Hospital hospital = doctor.getHospital();
            if (hospital == null)
                throw new BusinessException("Doctor is not associated with any hospital");

            Patient patient;
            if (hasAnyRole(currentUser, "ADMIN", "STAFF", "SUPER_ADMIN")) {
                if (request.getPatientId() == null)
                    throw new BusinessException("Patient ID is required for staff booking");
                patient = patientRepository.findById(request.getPatientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            } else {
                patient = patientRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for current user"));
            }

            // check if slot is already occupied in DB
            boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
                    request.getDoctorId(), request.getAppointmentDate(), request.getStartTime(),
                    List.of(Appointment.AppointmentStatus.CANCELLED, Appointment.AppointmentStatus.NO_SHOW));

            if (exists) {
                throw new SlotUnavailableException("This time slot is already booked.");
            }

            // 2. Map and Save
            Appointment appointment = appointmentMapper.toEntity(request);
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setService(service);
            appointment.setHospital(hospital);
            appointment.setBookedBy(currentUser);

            LocalTime endTime = request.getStartTime().plusMinutes(service.getDurationMinutes());
            appointment.setEndTime(endTime);
            appointment.setStatus(Appointment.AppointmentStatus.PENDING);

            Appointment saved = appointmentRepository.save(appointment);

            // TODO: Create Payment record (will be implemented in Payments module)
            // TODO: Send notification (will be implemented in Notifications module)

            appointmentBookedCounter.increment();
            return appointmentMapper.toResponse(saved);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(UUID id, User currentUser) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        validateOwnership(appointment, currentUser);

        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummaryResponse> getAppointments(AppointmentFilterRequest filters, Pageable pageable,
            User currentUser) {
        // Enforce role-based scoping
        UUID patientId = null;
        UUID doctorId = null;

        if (hasRole(currentUser, "PATIENT")) {
            patientId = patientRepository.findByUserId(currentUser.getId())
                    .map(Patient::getId).orElseThrow(() -> new BusinessException("Patient profile not found"));
        } else if (hasRole(currentUser, "DOCTOR")) {
            doctorId = doctorRepository.findByUserId(currentUser.getId())
                    .map(Doctor::getId).orElseThrow(() -> new BusinessException("Doctor profile not found"));
        } else if (hasAnyRole(currentUser, "ADMIN", "STAFF", "SUPER_ADMIN")) {
            patientId = filters.getPatientId();
            doctorId = filters.getDoctorId();
        } else {
            throw new AccessDeniedException("Insufficient permissions");
        }

        Page<Appointment> page = appointmentRepository.findAppointments(
                doctorId != null ? doctorId : filters.getDoctorId(),
                patientId != null ? patientId : filters.getPatientId(),
                filters.getHospitalId(),
                filters.getStatus(),
                filters.getType(),
                filters.getDateFrom(),
                filters.getDateTo(),
                pageable);

        return PageResponse.fromPage(page.map(appointmentMapper::toSummaryResponse));
    }

    @Override
    @Transactional
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "CONFIRM_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse confirmAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new BusinessException(
                    "Only PENDING appointments can be confirmed. Current status: " + appointment.getStatus());
        }

        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "START_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse startAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED appointments can be started.");
        }

        appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "COMPLETE_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse completeAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != Appointment.AppointmentStatus.IN_PROGRESS) {
            throw new BusinessException("Only IN_PROGRESS appointments can be completed.");
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "CANCEL_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse cancelAppointment(UUID id, String reason, User currentUser) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        validateOwnership(appointment, currentUser);

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Completed appointments cannot be cancelled.");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledBy(currentUser);

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "RESCHEDULE_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse rescheduleAppointment(UUID id, RescheduleAppointmentRequest request, User currentUser) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        validateOwnership(appointment, currentUser);

        if (Arrays.asList(Appointment.AppointmentStatus.COMPLETED, Appointment.AppointmentStatus.CANCELLED)
                .contains(appointment.getStatus())) {
            throw new BusinessException("Completed or cancelled appointments cannot be rescheduled.");
        }

        String lockKey = LOCK_PREFIX + appointment.getDoctor().getId() + ":" + request.getNewDate() + ":"
                + request.getNewStartTime();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(locked)) {
            throw new SlotUnavailableException("The new time slot is currently being locked by another user.");
        }

        try {
            boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
                    appointment.getDoctor().getId(), request.getNewDate(), request.getNewStartTime(),
                    List.of(Appointment.AppointmentStatus.CANCELLED, Appointment.AppointmentStatus.NO_SHOW));

            if (exists) {
                throw new SlotUnavailableException("The new time slot is already booked.");
            }

            appointment.setAppointmentDate(request.getNewDate());
            appointment.setStartTime(request.getNewStartTime());
            appointment
                    .setEndTime(request.getNewStartTime().plusMinutes(appointment.getService().getDurationMinutes()));
            appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
            appointment.setCancellationReason(request.getReason()); // Using this for reschedule reason too

            return appointmentMapper.toResponse(appointmentRepository.save(appointment));
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentStatsResponse getStats(User currentUser) {
        // Enforce role-based scoping
        UUID patientId = null;
        UUID doctorId = null;

        if (hasRole(currentUser, "PATIENT")) {
            patientId = patientRepository.findByUserId(currentUser.getId())
                    .map(Patient::getId).orElseThrow(() -> new BusinessException("Patient profile not found"));
        } else if (hasRole(currentUser, "DOCTOR")) {
            doctorId = doctorRepository.findByUserId(currentUser.getId())
                    .map(Doctor::getId).orElseThrow(() -> new BusinessException("Doctor profile not found"));
        }

        List<Object[]> results = appointmentRepository.countAppointmentsByStatus(doctorId, patientId);

        long total = 0;
        long pending = 0;
        long confirmed = 0;
        long completed = 0;
        long cancelled = 0;

        for (Object[] result : results) {
            Appointment.AppointmentStatus status = (Appointment.AppointmentStatus) result[0];
            long count = (Long) result[1];
            total += count;
            switch (status) {
                case PENDING -> pending = count;
                case CONFIRMED -> confirmed = count;
                case COMPLETED -> completed = count;
                case CANCELLED -> cancelled = count;
                default -> {
                } // Other statuses like NO_SHOW, IN_PROGRESS, RESCHEDULED are not specifically
                  // tracked in main counters
            }
        }

        return new AppointmentStatsResponse(total, pending, confirmed, completed, cancelled, new HashMap<>());
    }

    @Override
    @Transactional
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "BULK_CANCEL_APPOINTMENTS", entityName = "Appointment")
    public void bulkCancelAppointments(UUID doctorId, java.time.LocalDate date, String reason, User currentUser) {
        List<Appointment> appointments = appointmentRepository.findOccupiedSlots(doctorId, date);

        for (Appointment appointment : appointments) {
            appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
            appointment.setCancellationReason(reason);
            appointment.setCancelledBy(currentUser);
        }

        appointmentRepository.saveAll(appointments);
    }

    private void validateOwnership(Appointment appointment, User currentUser) {
        if (hasAnyRole(currentUser, "ADMIN", "STAFF", "SUPER_ADMIN")) {
            return;
        }

        if (hasRole(currentUser, "PATIENT")) {
            if (!appointment.getPatient().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your appointment");
            }
        } else if (hasRole(currentUser, "DOCTOR")) {
            if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your appointment");
            }
        }
    }

    private boolean hasRole(User user, String role) {
        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(roleName));
    }

    private boolean hasAnyRole(User user, String... roles) {
        return Arrays.stream(roles).anyMatch(role -> hasRole(user, role));
    }
}
