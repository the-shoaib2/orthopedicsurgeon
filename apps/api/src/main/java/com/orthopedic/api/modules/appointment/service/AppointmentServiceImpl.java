package com.orthopedic.api.modules.appointment.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.dto.request.AppointmentFilterRequest;
import com.orthopedic.api.modules.appointment.dto.request.BookAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.mapper.AppointmentMapper;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.entity.Hospital;
import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import com.orthopedic.api.modules.hospital.repository.HospitalRepository;
import com.orthopedic.api.modules.hospital.repository.ServiceRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import com.orthopedic.api.shared.exception.SlotUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentMapper appointmentMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final io.micrometer.core.instrument.Counter appointmentBookedCounter;

    private static final String LOCK_PREFIX = "lock:appointment:";

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "BOOK_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse bookAppointment(BookAppointmentRequest request, User currentUser) {
        String lockKey = LOCK_PREFIX + request.getDoctorId() + ":" + request.getAppointmentDate() + ":" + request.getStartTime();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(30));
        
        if (Boolean.FALSE.equals(locked)) {
            throw new SlotUnavailableException("This time slot is currently being booked by another user. Please try again in 30 seconds.");
        }

        try {
            // 1. Validation
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            ServiceEntity service = serviceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

            // Ensure service belongs to doctor's hospital or same hospital mentioned
            Hospital hospital = doctor.getHospital();
            if (hospital == null) throw new BusinessException("Doctor is not associated with any hospital");

            Patient patient;
            if (Arrays.asList("ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN").stream()
                    .anyMatch(role -> currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role)))) {
                if (request.getPatientId() == null) throw new BusinessException("Patient ID is required for staff booking");
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
    public PageResponse<AppointmentSummaryResponse> getAppointments(AppointmentFilterRequest filters, Pageable pageable, User currentUser) {
        // Enforce role-based scoping
        UUID patientId = null;
        UUID doctorId = null;

        if (hasRole(currentUser, "ROLE_PATIENT")) {
            patientId = patientRepository.findByUserId(currentUser.getId())
                    .map(Patient::getId).orElseThrow(() -> new BusinessException("Patient profile not found"));
        } else if (hasRole(currentUser, "ROLE_DOCTOR")) {
            doctorId = doctorRepository.findByUserId(currentUser.getId())
                    .map(Doctor::getId).orElseThrow(() -> new BusinessException("Doctor profile not found"));
        } else if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
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
                pageable
        );
        
        return PageResponse.fromPage(page.map(appointmentMapper::toSummaryResponse));
    }

    @Override
    @Transactional
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "CONFIRM_APPOINTMENT", entityName = "Appointment")
    public AppointmentResponse confirmAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new BusinessException("Only PENDING appointments can be confirmed. Current status: " + appointment.getStatus());
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
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

    private void validateOwnership(Appointment appointment, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            if (!appointment.getPatient().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your appointment");
            }
        } else if (hasRole(currentUser, "ROLE_DOCTOR")) {
            if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your appointment");
            }
        }
    }

    private boolean hasRole(User user, String role) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    private boolean hasAnyRole(User user, String... roles) {
        List<String> roleList = Arrays.asList(roles);
        return user.getAuthorities().stream().anyMatch(a -> roleList.contains(a.getAuthority()));
    }
}
