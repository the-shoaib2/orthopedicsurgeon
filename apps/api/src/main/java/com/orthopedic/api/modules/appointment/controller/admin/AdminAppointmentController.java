package com.orthopedic.api.modules.appointment.controller.admin;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.dto.request.AppointmentFilterRequest;
import com.orthopedic.api.modules.appointment.dto.request.BookAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.request.RescheduleAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentStatsResponse;
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
@RequestMapping("/api/v1/admin/appointments")
@Tag(name = "Admin Appointment Management", description = "Endpoints for administrators and staff to manage all appointments")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
public class AdminAppointmentController extends BaseController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @Operation(summary = "List all appointments with filters")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentSummaryResponse>>> getAll(
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

    @GetMapping("/stats")
    @Operation(summary = "Get global appointment statistics")
    public ResponseEntity<ApiResponse<AppointmentStatsResponse>> getStats(@CurrentUser User currentUser) {
        return ok(appointmentService.getStats(currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment detail by ID")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        return ok(appointmentService.getAppointmentById(id, currentUser));
    }

    @PostMapping
    @Operation(summary = "Book a new appointment for a patient")
    public ResponseEntity<ApiResponse<AppointmentResponse>> book(
            @Valid @RequestBody BookAppointmentRequest request,
            @CurrentUser User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully",
                        appointmentService.bookAppointment(request, currentUser)));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm an appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> confirm(@PathVariable UUID id) {
        return ok("Appointment confirmed", appointmentService.confirmAppointment(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancel(
            @PathVariable UUID id,
            @RequestParam String reason,
            @CurrentUser User currentUser) {
        return ok("Appointment cancelled", appointmentService.cancelAppointment(id, reason, currentUser));
    }

    @PostMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reschedule(
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleAppointmentRequest request,
            @CurrentUser User currentUser) {
        return ok("Appointment rescheduled", appointmentService.rescheduleAppointment(id, request, currentUser));
    }

    @PostMapping("/bulk-cancel")
    @Operation(summary = "Bulk cancel all appointments for a doctor on a specific date")
    public ResponseEntity<ApiResponse<Void>> bulkCancel(
            @RequestParam UUID doctorId,
            @RequestParam java.time.LocalDate date,
            @RequestParam String reason,
            @CurrentUser User currentUser) {
        appointmentService.bulkCancelAppointments(doctorId, date, reason, currentUser);
        return ok("Bulk cancellation completed successfully", null);
    }
}
