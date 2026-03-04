package com.orthopedic.api.modules.health.controller.admin;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.health.dto.request.RecordVitalsRequest;
import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import com.orthopedic.api.modules.health.entity.PatientTimeline;
import com.orthopedic.api.modules.health.service.impl.HealthRecordServiceImpl;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
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
@RequestMapping("/api/v1/admin/health")
@RequiredArgsConstructor
@Tag(name = "Admin Health", description = "Endpoints for provider-side health records management")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
public class AdminHealthController extends BaseController {

    private final HealthRecordServiceImpl healthRecordService;

    @PostMapping("/patients/{patientId}/vitals")
    @Operation(summary = "Record vital signs for a patient")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> recordPatientVitals(
            @PathVariable UUID patientId,
            @Valid @RequestBody RecordVitalsRequest request,
            @CurrentUser User currentUser) {
        return ok(healthRecordService.recordVitals(patientId, request, currentUser.getId()));
    }

    @GetMapping("/patients/{patientId}/vitals")
    @Operation(summary = "Get vital signs history for a patient")
    public ResponseEntity<ApiResponse<PageResponse<VitalSignsResponse>>> getPatientVitalHistory(
            @PathVariable UUID patientId,
            Pageable pageable) {
        return ok(healthRecordService.getVitalHistory(patientId, pageable));
    }

    @GetMapping("/patients/{patientId}/timeline")
    @Operation(summary = "Get clinical timeline for a patient")
    public ResponseEntity<ApiResponse<PageResponse<PatientTimeline>>> getPatientTimeline(
            @PathVariable UUID patientId,
            Pageable pageable) {
        return ok(healthRecordService.getPatientTimeline(patientId, pageable));
    }
}
