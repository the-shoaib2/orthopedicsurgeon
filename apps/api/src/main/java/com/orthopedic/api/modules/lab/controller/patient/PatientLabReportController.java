package com.orthopedic.api.modules.lab.controller.patient;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.lab.dto.response.LabReportResponse;
import com.orthopedic.api.modules.lab.service.LabReportService;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import com.orthopedic.api.shared.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient/lab-reports")
@Tag(name = "Patient Lab Report Management", description = "Endpoints for patients to view their own lab reports")
@PreAuthorize("hasRole('PATIENT')")
public class PatientLabReportController extends BaseController {

    private final LabReportService labReportService;
    private final PatientRepository patientRepository;

    public PatientLabReportController(LabReportService labReportService, PatientRepository patientRepository) {
        this.labReportService = labReportService;
        this.patientRepository = patientRepository;
    }

    @GetMapping
    @Operation(summary = "Get my list of lab reports")
    public ResponseEntity<ApiResponse<PageResponse<LabReportResponse>>> getMyReports(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Collections.singletonList("createdAt"));

        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return ok(labReportService.getPatientReports(patient.getId(), pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab report detail by ID")
    public ResponseEntity<ApiResponse<LabReportResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(labReportService.getReportById(id, currentUser));
    }
}
