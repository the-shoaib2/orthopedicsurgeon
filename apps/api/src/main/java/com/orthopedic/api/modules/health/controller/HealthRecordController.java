package com.orthopedic.api.modules.health.controller;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.health.dto.request.RecordVitalsRequest;
import com.orthopedic.api.modules.health.dto.response.PatientDashboardResponse;
import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import com.orthopedic.api.modules.health.entity.PatientTimeline;
import com.orthopedic.api.modules.health.service.impl.HealthRecordServiceImpl;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient/health")
@RequiredArgsConstructor
@Tag(name = "Patient Health", description = "Endpoints for patient health records")
public class HealthRecordController extends BaseController {

    private final HealthRecordServiceImpl healthRecordService;
    private final PatientRepository patientRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get patient health dashboard")
    public ResponseEntity<ApiResponse<PatientDashboardResponse>> getDashboard(@CurrentUser User currentUser) {
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));
        return ok(healthRecordService.getPatientDashboard(patient.getId()));
    }

    @PostMapping("/vitals")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    @Operation(summary = "Record vital signs")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> recordVitals(
            @Valid @RequestBody RecordVitalsRequest request,
            @CurrentUser User currentUser) {
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));
        return ok(healthRecordService.recordVitals(patient.getId(), request, currentUser.getId()));
    }

    @GetMapping("/vitals/history")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get vital signs history")
    public ResponseEntity<ApiResponse<PageResponse<VitalSignsResponse>>> getVitalHistory(
            @CurrentUser User currentUser,
            Pageable pageable) {
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));
        return ok(healthRecordService.getVitalHistory(patient.getId(), pageable));
    }

    @GetMapping("/timeline")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get patient timeline")
    public ResponseEntity<ApiResponse<PageResponse<PatientTimeline>>> getTimeline(
            @CurrentUser User currentUser,
            Pageable pageable) {
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));
        return ok(healthRecordService.getPatientTimeline(patient.getId(), pageable));
    }
}
