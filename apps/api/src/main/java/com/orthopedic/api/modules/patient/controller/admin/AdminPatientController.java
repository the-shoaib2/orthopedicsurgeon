package com.orthopedic.api.modules.patient.controller.admin;

import com.orthopedic.api.modules.patient.dto.request.CreatePatientRequest;
import com.orthopedic.api.modules.patient.dto.request.PatientFilterRequest;
import com.orthopedic.api.modules.patient.dto.response.PatientMedicalHistoryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientSummaryResponse;
import com.orthopedic.api.modules.patient.service.PatientService;
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

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/patients")
@Tag(name = "Admin Patient Management", description = "Endpoints for administrators to manage patient records and medical history")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
public class AdminPatientController extends BaseController {

    private final PatientService patientService;

    public AdminPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "List all patients for administration")
    public ResponseEntity<ApiResponse<PageResponse<PatientSummaryResponse>>> getAll(
            PatientFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Arrays.asList("dateOfBirth", "createdAt", "status"));

        return ok(patientService.getAllPatients(filters, pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new patient record")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient record created successfully",
                        patientService.createPatient(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full patient profile")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable UUID id) {
        return ok(patientService.getPatientById(id));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get complete medical history of a patient")
    public ResponseEntity<ApiResponse<PatientMedicalHistoryResponse>> getHistory(@PathVariable UUID id) {
        return ok(patientService.getMedicalHistory(id));
    }
}
