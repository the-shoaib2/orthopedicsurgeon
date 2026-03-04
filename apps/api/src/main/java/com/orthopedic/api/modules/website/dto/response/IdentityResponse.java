package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;

@Builder
public record IdentityResponse(
        String logoUrl,
        String faviconUrl,
        String siteName,
        String primaryColor,
        String secondaryColor) {
}
