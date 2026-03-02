package com.orthopedic.api.modules.doctor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Doctor availability slot information")
public class DoctorAvailabilityResponse {
    @Schema(description = "Day of the week", example = "MONDAY")
    private DayOfWeek dayOfWeek;

    @Schema(description = "Shift start time", example = "09:00")
    private LocalTime startTime;

    @Schema(description = "Shift end time", example = "17:00")
    private LocalTime endTime;

    @Schema(description = "Availability status for this day", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Maximum appointments allowed per slot", example = "1")
    private Integer maxAppointmentsPerSlot;

    @Schema(description = "Number of currently available slots", example = "10")
    private Integer availableSlots;
}
