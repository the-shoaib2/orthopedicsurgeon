package com.orthopedic.api.modules.lab.controller.admin;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.lab.dto.request.CreateLabReportRequest;
import com.orthopedic.api.modules.lab.dto.request.UpdateLabReportResultRequest;
import com.orthopedic.api.modules.lab.dto.response.LabReportResponse;
import com.orthopedic.api.modules.lab.service.LabReportService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/lab-reports")
@Tag(name = "Admin Lab Report Management", description = "Endpoints for administrators and staff to create and manage all lab report requests")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
public class AdminLabReportController extends BaseController {

    private final LabReportService labReportService;

    public AdminLabReportController(LabReportService labReportService) {
        this.labReportService = labReportService;
    }

    @PostMapping
    @Operation(summary = "Create a new lab report request")
    public ResponseEntity<ApiResponse<LabReportResponse>> createRequest(
            @Valid @RequestBody CreateLabReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lab report request created successfully",
                        labReportService.createReportRequest(request)));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get list of lab reports for any patient")
    public ResponseEntity<ApiResponse<PageResponse<LabReportResponse>>> getByPatient(
            @PathVariable UUID patientId,
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Collections.singletonList("createdAt"));

        return ok(labReportService.getPatientReports(patientId, pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab report detail by ID")
    public ResponseEntity<ApiResponse<LabReportResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(labReportService.getReportById(id, currentUser));
    }

    @PatchMapping("/{id}/result")
    @Operation(summary = "Update lab report result")
    public ResponseEntity<ApiResponse<LabReportResponse>> updateResult(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLabReportResultRequest request) {
        return ok("Lab report result updated successfully",
                labReportService.updateReportResult(id, request));
    }
}
