package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record TeamMemberResponse(
        UUID id,
        String name,
        String role,
        String specialization,
        String bio,
        String photoUrl,
        int displayOrder,
        String facebookUrl,
        String twitterUrl,
        String linkedinUrl) {
}
