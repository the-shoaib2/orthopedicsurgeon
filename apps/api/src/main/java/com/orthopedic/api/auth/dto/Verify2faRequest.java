package com.orthopedic.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Verify2faRequest {
    @NotBlank(message = "Temporary token is required")
    private String tempToken;

    @NotBlank(message = "TOTP code is required")
    private String totpCode;
}
