package com.orthopedic.api.modules.doctor.controller.admin;

import com.orthopedic.api.modules.doctor.dto.request.CreateDoctorRequest;
import com.orthopedic.api.modules.doctor.dto.request.DoctorFilterRequest;
import com.orthopedic.api.modules.doctor.dto.response.DoctorResponse;
import com.orthopedic.api.modules.doctor.dto.response.DoctorSummaryResponse;
import com.orthopedic.api.modules.doctor.service.DoctorService;
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
@RequestMapping("/api/v1/admin/doctors")
@Tag(name = "Admin Doctor Management", description = "Endpoints for administrators to manage doctor profiles and registrations")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminDoctorController extends BaseController {

    private final DoctorService doctorService;

    public AdminDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    @Operation(summary = "List all doctors for administration")
    public ResponseEntity<ApiResponse<PageResponse<DoctorSummaryResponse>>> getAll(
            DoctorFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Arrays.asList("specialization", "experienceYears", "status", "createdAt"));

        return ok(doctorService.getAllDoctors(filters, pageable));
    }

    @PostMapping
    @Operation(summary = "Register a new doctor")
    public ResponseEntity<ApiResponse<DoctorResponse>> create(@Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor registered successfully",
                        doctorService.createDoctor(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full doctor details")
    public ResponseEntity<ApiResponse<DoctorResponse>> getById(@PathVariable UUID id) {
        return ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update doctor profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody CreateDoctorRequest request) {
        return ok(doctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete doctor profile")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ok("Doctor deleted successfully", null);
    }
}
