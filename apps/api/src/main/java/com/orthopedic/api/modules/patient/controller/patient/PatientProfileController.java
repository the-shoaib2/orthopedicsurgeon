package com.orthopedic.api.modules.patient.controller.patient;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.patient.dto.response.PatientMedicalHistoryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.service.PatientService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient/profile")
@Tag(name = "Patient Profile Management", description = "Endpoints for patients to manage their own medical profiles")
@PreAuthorize("hasRole('PATIENT')")
public class PatientProfileController extends BaseController {

    private final PatientService patientService;

    public PatientProfileController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get my patient profile")
    public ResponseEntity<ApiResponse<PatientResponse>> getMyProfile(@CurrentUser User currentUser) {
        // Implementation would typically lookup patient by current user ID
        // For now, assuming service has a method for this
        return ok(patientService.getPatientByUserId(currentUser.getId()));
    }

    @GetMapping("/history")
    @Operation(summary = "Get my own medical history")
    public ResponseEntity<ApiResponse<PatientMedicalHistoryResponse>> getMyHistory(@CurrentUser User currentUser) {
        // Need to get patient ID first
        UUID patientId = patientService.getPatientByUserId(currentUser.getId()).getId();
        return ok(patientService.getMedicalHistory(patientId));
    }
}
