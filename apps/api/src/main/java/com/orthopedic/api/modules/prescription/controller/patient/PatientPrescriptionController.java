package com.orthopedic.api.modules.prescription.controller.patient;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.prescription.dto.response.PrescriptionResponse;
import com.orthopedic.api.modules.prescription.service.PrescriptionService;
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
@RequestMapping("/api/v1/patient/prescriptions")
@Tag(name = "Patient Prescription Management", description = "Endpoints for patients to view their own prescriptions")
@PreAuthorize("hasRole('PATIENT')")
public class PatientPrescriptionController extends BaseController {

    private final PrescriptionService prescriptionService;
    private final PatientRepository patientRepository;

    public PatientPrescriptionController(PrescriptionService prescriptionService, PatientRepository patientRepository) {
        this.prescriptionService = prescriptionService;
        this.patientRepository = patientRepository;
    }

    @GetMapping
    @Operation(summary = "Get my list of prescriptions")
    public ResponseEntity<ApiResponse<PageResponse<PrescriptionResponse>>> getMyPrescriptions(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Collections.singletonList("createdAt"));

        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return ok(prescriptionService.getPatientPrescriptions(patient.getId(), pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription detail by ID")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(prescriptionService.getPrescriptionById(id, currentUser));
    }
}
