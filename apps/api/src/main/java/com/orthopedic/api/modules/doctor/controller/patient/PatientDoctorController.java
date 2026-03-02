package com.orthopedic.api.modules.doctor.controller.patient;

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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient/doctors")
@Tag(name = "Patient Doctor Discovery", description = "Endpoints for patients to find doctors and view their profiles")
public class PatientDoctorController extends BaseController {

    private final DoctorService doctorService;

    public PatientDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    @Operation(summary = "Search for available doctors")
    public ResponseEntity<ApiResponse<PageResponse<DoctorSummaryResponse>>> search(
            DoctorFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Arrays.asList("specialization", "experienceYears", "consultationFee"));

        return ok(doctorService.getAllDoctors(filters, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed doctor profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> getById(@PathVariable UUID id) {
        return ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{id}/slots")
    @Operation(summary = "Get available time slots for a specific date")
    public ResponseEntity<ApiResponse<List<LocalTime>>> getSlots(
            @PathVariable UUID id,
            @RequestParam LocalDate date) {
        return ok(doctorService.getAvailableSlots(id, date));
    }
}
