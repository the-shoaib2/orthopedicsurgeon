package com.orthopedic.api.modules.booking.controller;

import com.orthopedic.api.modules.booking.dto.request.BookingRequest;
import com.orthopedic.api.modules.booking.dto.response.BookingResponse;
import com.orthopedic.api.modules.booking.dto.response.SlotResponse;
import com.orthopedic.api.modules.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * I-01: Get available slots for a doctor on a specific date.
     */
    @GetMapping("/slots")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(doctorId, date));
    }

    /**
     * I-02: Get available slots for a doctor for a full week.
     */
    @GetMapping("/slots/week")
    public ResponseEntity<List<SlotResponse>> getWeeklySlots(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(bookingService.getAvailableSlotsForWeek(doctorId, startDate));
    }

    /**
     * I-03: Book an appointment (Authenticated patients only).
     */
    @PostMapping
    public ResponseEntity<BookingResponse> bookAppointment(
            @Valid @RequestBody BookingRequest request,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(bookingService.bookAppointment(userId, request));
    }

    /**
     * I-04: Get a specific booking detail.
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<BookingResponse> getBooking(
            @PathVariable UUID appointmentId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(bookingService.getBooking(appointmentId, userId));
    }

    /**
     * I-05: Get all my appointments, optionally filtered by status.
     */
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyAppointments(
            @RequestParam UUID userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(bookingService.getMyAppointments(userId, status));
    }

    /**
     * I-06: Cancel an appointment.
     */
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID appointmentId,
            @RequestParam UUID userId,
            @RequestBody Map<String, String> body) {
        bookingService.cancelBooking(appointmentId, userId, body.get("reason"));
        return ResponseEntity.ok().build();
    }

    /**
     * I-07: Reschedule an appointment.
     */
    @PatchMapping("/{appointmentId}/reschedule")
    public ResponseEntity<Void> rescheduleBooking(
            @PathVariable UUID appointmentId,
            @RequestParam UUID userId,
            @RequestBody Map<String, UUID> body) {
        bookingService.rescheduleBooking(appointmentId, userId, body.get("newSlotId"));
        return ResponseEntity.ok().build();
    }
}
