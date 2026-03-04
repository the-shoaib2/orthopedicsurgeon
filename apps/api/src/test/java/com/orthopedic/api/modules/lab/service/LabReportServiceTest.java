package com.orthopedic.api.modules.lab.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.lab.dto.request.CreateLabReportRequest;
import com.orthopedic.api.modules.lab.dto.request.UpdateLabReportResultRequest;
import com.orthopedic.api.modules.lab.dto.response.LabReportResponse;
import com.orthopedic.api.modules.lab.entity.LabReport;
import com.orthopedic.api.modules.lab.mapper.LabReportMapper;
import com.orthopedic.api.modules.lab.repository.LabReportRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabReportServiceTest {

    @Mock
    private LabReportRepository labReportRepository;
    @Mock
    private LabReportMapper labReportMapper;
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private LabReportServiceImpl labReportService;

    private User currentUser;
    private LabReport report;
    private Patient patient;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        currentUser.setEmail("patient@test.com");
        currentUser.setRoles(Collections.emptySet()); // Updated based on User entity structure if needed

        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setUser(currentUser);

        report = new LabReport();
        report.setId(UUID.randomUUID());
        report.setPatient(patient);
        report.setStatus(LabReport.LabReportStatus.PENDING);
    }

    @Test
    void createReportRequest_Success() {
        CreateLabReportRequest request = new CreateLabReportRequest();
        request.setPatientId(patient.getId());

        when(labReportMapper.toEntity(any())).thenReturn(report);
        when(labReportRepository.save(any())).thenReturn(report);
        when(labReportMapper.toResponse(any())).thenReturn(new LabReportResponse());

        LabReportResponse response = labReportService.createReportRequest(request);

        assertNotNull(response);
        verify(labReportRepository).save(any());
    }

    @Test
    void getReportById_Success() {
        when(labReportRepository.findById(report.getId())).thenReturn(Optional.of(report));
        when(labReportMapper.toResponse(report)).thenReturn(new LabReportResponse());

        LabReportResponse response = labReportService.getReportById(report.getId(), currentUser);

        assertNotNull(response);
    }

    @Test
    void getReportById_AccessDenied() {
        User otherUser = new User();
        otherUser.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        otherUser.setEmail("other@test.com");
        otherUser.setRoles(Collections.emptySet());

        when(labReportRepository.findById(report.getId())).thenReturn(Optional.of(report));

        assertThrows(AccessDeniedException.class, () -> labReportService.getReportById(report.getId(), otherUser));
    }

    @Test
    void updateReportResult_Success() {
        UpdateLabReportResultRequest request = new UpdateLabReportResultRequest();
        request.setResultSummary("Normal");

        when(labReportRepository.findById(report.getId())).thenReturn(Optional.of(report));
        when(labReportRepository.save(any())).thenReturn(report);
        when(labReportMapper.toResponse(any())).thenReturn(new LabReportResponse());

        LabReportResponse response = labReportService.updateReportResult(report.getId(), request);

        assertNotNull(response);
        assertEquals(LabReport.LabReportStatus.COMPLETED, report.getStatus());
    }
}
