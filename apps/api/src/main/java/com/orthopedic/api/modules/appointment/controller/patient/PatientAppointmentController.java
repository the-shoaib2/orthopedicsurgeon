package com.orthopedic.api.modules.appointment.controller.patient;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.dto.request.AppointmentFilterRequest;
import com.orthopedic.api.modules.appointment.dto.request.BookAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.modules.appointment.service.AppointmentService;
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

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient/appointments")
@Tag(name = "Patient Appointment Management", description = "Endpoints for patients to book and manage their own appointments")
@PreAuthorize("hasRole('PATIENT')")
public class PatientAppointmentController extends BaseController {

    private final AppointmentService appointmentService;

    public PatientAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @Operation(summary = "Get list of my appointments")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentSummaryResponse>>> getMyAppointments(
            AppointmentFilterRequest filters,
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Arrays.asList("appointmentDate", "startTime", "status", "createdAt"));

        return ok(appointmentService.getAppointments(filters, pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get my appointment detail by ID")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(appointmentService.getAppointmentById(id, currentUser));
    }

    @PostMapping
    @Operation(summary = "Book a new appointment for myself")
    public ResponseEntity<ApiResponse<AppointmentResponse>> book(
            @Valid @RequestBody BookAppointmentRequest request,
            @CurrentUser User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully",
                        appointmentService.bookAppointment(request, currentUser)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel my appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancel(
            @PathVariable UUID id,
            @RequestParam String reason,
            @CurrentUser User currentUser) {
        return ok("Appointment cancelled", appointmentService.cancelAppointment(id, reason, currentUser));
    }
}
