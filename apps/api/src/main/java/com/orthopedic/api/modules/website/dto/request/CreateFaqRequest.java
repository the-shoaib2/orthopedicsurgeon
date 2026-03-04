package com.orthopedic.api.modules.website.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateFaqRequest(
                @NotBlank @Size(max = 500) String question,
                @NotBlank @Size(max = 2000) String answer,
                @NotBlank String category,
                @Min(0) int displayOrder,
                boolean isActive) {
}
