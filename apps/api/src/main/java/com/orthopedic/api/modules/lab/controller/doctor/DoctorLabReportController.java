package com.orthopedic.api.modules.lab.controller.doctor;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.lab.dto.request.UpdateLabReportResultRequest;
import com.orthopedic.api.modules.lab.dto.response.LabReportResponse;
import com.orthopedic.api.modules.lab.service.LabReportService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import com.orthopedic.api.shared.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctor/lab-reports")
@Tag(name = "Doctor Lab Report Management", description = "Endpoints for doctors to view and update lab reports for their patients")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorLabReportController extends BaseController {

    private final LabReportService labReportService;
    private final DoctorRepository doctorRepository;

    public DoctorLabReportController(LabReportService labReportService, DoctorRepository doctorRepository) {
        this.labReportService = labReportService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    @Operation(summary = "Get list of lab reports for my patients")
    public ResponseEntity<ApiResponse<PageResponse<LabReportResponse>>> getMyPatientReports(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Collections.singletonList("createdAt"));

        Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        return ok(labReportService.getDoctorReports(doctor.getId(), pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab report detail by ID")
    public ResponseEntity<ApiResponse<LabReportResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(labReportService.getReportById(id, currentUser));
    }

    @PatchMapping("/{id}/result")
    @Operation(summary = "Update lab report result (Doctor only)")
    public ResponseEntity<ApiResponse<LabReportResponse>> updateResult(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLabReportResultRequest request) {
        return ok("Lab report result updated successfully",
                labReportService.updateReportResult(id, request));
    }
}
