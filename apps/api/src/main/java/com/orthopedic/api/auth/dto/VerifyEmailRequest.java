package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @NotBlank(message = "Verification token is required")
    @Schema(description = "Verification token sent via email", example = "550e8400-e29b-41d4-a716-446655440000")
    private String token;
}
