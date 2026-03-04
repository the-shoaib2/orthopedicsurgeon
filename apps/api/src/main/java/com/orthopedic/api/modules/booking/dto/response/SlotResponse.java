package com.orthopedic.api.modules.booking.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record SlotResponse(
        UUID id,
        UUID doctorId,
        LocalDate date,
        LocalTime time,
        boolean isBooked) {
}
