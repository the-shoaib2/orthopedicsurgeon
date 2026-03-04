package com.orthopedic.api.modules.appointment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for rescheduling an appointment")
public class RescheduleAppointmentRequest {
    @NotNull(message = "New appointment date is required")
    @FutureOrPresent(message = "New appointment date must be today or in the future")
    @Schema(description = "New planned date for the appointment", example = "2024-12-26")
    private LocalDate newDate;

    @NotNull(message = "New start time is required")
    @Schema(description = "New planned start time", example = "11:30")
    private LocalTime newStartTime;

    @Schema(description = "Reason for rescheduling", example = "Patient requested later time")
    private String reason;
}
