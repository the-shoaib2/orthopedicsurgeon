package com.orthopedic.api.modules.patient.dto.response;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record AppointmentSummaryResponse(
        UUID id,
        String doctorName,
        String specialization,
        String serviceName,
        LocalDate date,
        LocalTime time,
        Appointment.AppointmentStatus status,
        Appointment.AppointmentType type) {
}
