package com.orthopedic.api.modules.prescription.controller.doctor;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.prescription.dto.request.CreatePrescriptionRequest;
import com.orthopedic.api.modules.prescription.dto.response.PrescriptionResponse;
import com.orthopedic.api.modules.prescription.service.PrescriptionService;
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
@RequestMapping("/api/v1/doctor/prescriptions")
@Tag(name = "Doctor Prescription Management", description = "Endpoints for doctors to issue and manage prescriptions")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorPrescriptionController extends BaseController {

    private final PrescriptionService prescriptionService;

    public DoctorPrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @Operation(summary = "Issue a new prescription")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> create(
            @Valid @RequestBody CreatePrescriptionRequest request,
            @CurrentUser User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Prescription issued successfully",
                        prescriptionService.createPrescription(request, currentUser)));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get list of prescriptions for a specific patient")
    public ResponseEntity<ApiResponse<PageResponse<PrescriptionResponse>>> getByPatient(
            @PathVariable UUID patientId,
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Collections.singletonList("createdAt"));

        return ok(prescriptionService.getPatientPrescriptions(patientId, pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription detail by ID")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(prescriptionService.getPrescriptionById(id, currentUser));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get prescription for a specific appointment")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getByAppointment(
            @PathVariable UUID appointmentId,
            @CurrentUser User currentUser) {
        return ok(prescriptionService.getPrescriptionByAppointment(appointmentId, currentUser));
    }
}
