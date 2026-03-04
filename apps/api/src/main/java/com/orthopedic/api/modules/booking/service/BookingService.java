package com.orthopedic.api.modules.booking.service;

import com.orthopedic.api.modules.booking.dto.request.BookingRequest;
import com.orthopedic.api.modules.booking.dto.response.BookingResponse;
import com.orthopedic.api.modules.booking.dto.response.SlotResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    // Group I: Booking Flow
    List<SlotResponse> getAvailableSlots(UUID doctorId, LocalDate date);

    List<SlotResponse> getAvailableSlotsForWeek(UUID doctorId, LocalDate startDate);

    BookingResponse bookAppointment(UUID userId, BookingRequest request);

    BookingResponse getBooking(UUID appointmentId, UUID userId);

    List<BookingResponse> getMyAppointments(UUID userId, String status);

    void cancelBooking(UUID appointmentId, UUID userId, String reason);

    void rescheduleBooking(UUID appointmentId, UUID userId, UUID newSlotId);
}
