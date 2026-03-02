package com.orthopedic.api.modules.lab.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.lab.dto.request.CreateLabReportRequest;
import com.orthopedic.api.modules.lab.dto.request.UpdateLabReportResultRequest;
import com.orthopedic.api.modules.lab.dto.response.LabReportResponse;
import com.orthopedic.api.modules.lab.entity.LabReport;
import com.orthopedic.api.modules.lab.mapper.LabReportMapper;
import com.orthopedic.api.modules.lab.repository.LabReportRepository;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LabReportServiceImpl implements LabReportService {

    private final LabReportRepository labReportRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final LabReportMapper labReportMapper;

    @Override
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "CREATE_LAB_REQUEST", entityName = "LabReport")
    public LabReportResponse createReportRequest(CreateLabReportRequest request) {
        LabReport report = labReportMapper.toEntity(request);
        
        if (request.getAppointmentId() != null) {
            report.setAppointment(appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found")));
        }
        
        report.setPatient(patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found")));
        
        if (request.getDoctorId() != null) {
            report.setDoctor(doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found")));
        }

        return labReportMapper.toResponse(labReportRepository.save(report));
    }

    @Override
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "UPDATE_LAB_RESULT", entityName = "LabReport")
    public LabReportResponse updateReportResult(UUID id, UpdateLabReportResultRequest request) {
        LabReport report = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab report not found"));
        
        report.setStatus(request.getStatus());
        report.setResultSummary(request.getResultSummary());
        report.setFilePath(request.getFilePath());
        
        if (request.getStatus() == LabReport.LabReportStatus.COMPLETED) {
            report.setReportDate(LocalDateTime.now());
        }

        return labReportMapper.toResponse(labReportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public LabReportResponse getReportById(UUID id, User currentUser) {
        LabReport report = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab report not found"));
        
        validateOwnership(report, currentUser);
        
        return labReportMapper.toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LabReportResponse> getPatientReports(UUID patientId, Pageable pageable, User currentUser) {
        validatePatientAccess(patientId, currentUser);
        Page<LabReport> page = labReportRepository.findAllByPatientId(patientId, pageable);
        return PageResponse.fromPage(page.map(labReportMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LabReportResponse> getDoctorReports(UUID doctorId, Pageable pageable, User currentUser) {
        validateDoctorAccess(doctorId, currentUser);
        Page<LabReport> page = labReportRepository.findAllByDoctorId(doctorId, pageable);
        return PageResponse.fromPage(page.map(labReportMapper::toResponse));
    }

    private void validatePatientAccess(UUID patientId, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            if (!patient.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your records");
            }
        } else {
            throw new AccessDeniedException("Access denied: Insufficient permissions");
        }
    }

    private void validateDoctorAccess(UUID doctorId, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        if (hasRole(currentUser, "ROLE_DOCTOR")) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            if (!doctor.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your records");
            }
        } else {
            throw new AccessDeniedException("Access denied: Insufficient permissions");
        }
    }

    private void validateOwnership(LabReport report, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            if (!report.getPatient().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your lab report");
            }
        } else if (hasRole(currentUser, "ROLE_DOCTOR")) {
            if (report.getDoctor() != null && !report.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your assigned report");
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
