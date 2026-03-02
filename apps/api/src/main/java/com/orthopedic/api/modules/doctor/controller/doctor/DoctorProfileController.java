package com.orthopedic.api.modules.doctor.controller.doctor;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.doctor.dto.response.DoctorResponse;
import com.orthopedic.api.modules.doctor.service.DoctorService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctor/profile")
@Tag(name = "Doctor Profile Management", description = "Endpoints for doctors to manage their own profiles")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorProfileController extends BaseController {

    private final DoctorService doctorService;

    public DoctorProfileController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    @Operation(summary = "Get my own doctor profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> getMyProfile(@CurrentUser User currentUser) {
        return ok(doctorService.getDoctorByUserId(currentUser.getId()));
    }
}
