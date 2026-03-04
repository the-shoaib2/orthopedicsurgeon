package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record GalleryItemResponse(
        UUID id,
        String title,
        String description,
        String imageUrl,
        String category,
        int displayOrder) {
}
