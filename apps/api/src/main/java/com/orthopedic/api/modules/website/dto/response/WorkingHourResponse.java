package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;

@Builder
public record WorkingHourResponse(
        String day,
        String hours,
        boolean isClosed) {
}
