package com.orthopedic.api.modules.prescription.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.prescription.dto.request.CreatePrescriptionRequest;
import com.orthopedic.api.modules.prescription.dto.response.PrescriptionResponse;
import com.orthopedic.api.modules.prescription.entity.Prescription;
import com.orthopedic.api.modules.prescription.entity.PrescriptionMedicine;
import com.orthopedic.api.modules.prescription.mapper.PrescriptionMapper;
import com.orthopedic.api.modules.prescription.repository.PrescriptionRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final com.orthopedic.api.modules.patient.repository.PatientRepository patientRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Override
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "CREATE_PRESCRIPTION", entityName = "Prescription")
    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request, User currentUser) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Security check: Only the assigned doctor can write a prescription
        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the assigned doctor can create a prescription for this appointment");
        }

        if (prescriptionRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new BusinessException("A prescription already exists for this appointment");
        }

        Prescription prescription = prescriptionMapper.toEntity(request);
        prescription.setAppointment(appointment);
        prescription.setPatient(appointment.getPatient());
        prescription.setDoctor(appointment.getDoctor());

        List<PrescriptionMedicine> medicines = request.getMedicines().stream()
                .map(prescriptionMapper::toMedicineEntity)
                .peek(m -> m.setPrescription(prescription))
                .collect(Collectors.toList());
        prescription.setMedicines(medicines);

        // Auto-complete appointment if it was in progress
        if (appointment.getStatus() == Appointment.AppointmentStatus.IN_PROGRESS) {
            appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);
        }

        return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(UUID id, User currentUser) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        
        validateOwnership(prescription, currentUser);
        
        return prescriptionMapper.toResponse(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByAppointment(UUID appointmentId, User currentUser) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for this appointment"));
        
        validateOwnership(prescription, currentUser);
        
        return prescriptionMapper.toResponse(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrescriptionResponse> getPatientPrescriptions(UUID patientId, Pageable pageable, User currentUser) {
        validatePatientAccess(patientId, currentUser);
        Page<Prescription> page = prescriptionRepository.findAllByPatientId(patientId, pageable);
        return PageResponse.fromPage(page.map(prescriptionMapper::toResponse));
    }

    private void validatePatientAccess(UUID patientId, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            com.orthopedic.api.modules.patient.entity.Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            if (!patient.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your records");
            }
        } else {
            throw new AccessDeniedException("Access denied: Insufficient permissions");
        }
    }

    private void validateOwnership(Prescription prescription, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            if (!prescription.getPatient().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your prescription");
            }
        } else if (hasRole(currentUser, "ROLE_DOCTOR")) {
            if (!prescription.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your prescription");
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
