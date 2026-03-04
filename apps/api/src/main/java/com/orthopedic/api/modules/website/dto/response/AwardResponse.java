package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record AwardResponse(
        UUID id,
        String title,
        String description,
        String imageUrl,
        String awardedBy,
        Integer awardYear,
        int displayOrder) {
}
