package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record PartnerResponse(
        UUID id,
        String name,
        String logoUrl,
        String websiteUrl,
        int displayOrder) {
}
