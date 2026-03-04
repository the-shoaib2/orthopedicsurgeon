package com.orthopedic.api.modules.booking.dto.response;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record BookingResponse(
        UUID appointmentId,
        String doctorName,
        String serviceName,
        LocalDate date,
        LocalTime startTime,
        Appointment.AppointmentStatus status) {
}
