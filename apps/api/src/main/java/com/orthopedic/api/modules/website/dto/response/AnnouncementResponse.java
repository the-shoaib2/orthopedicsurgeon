package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;

@Builder
public record AnnouncementResponse(
        String text,
        String link,
        String type, // INFO, WARNING, SUCCESS
        boolean isActive) {
}
