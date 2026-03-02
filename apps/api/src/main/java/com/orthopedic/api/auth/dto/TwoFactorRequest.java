package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for two-factor authentication verification")
public class TwoFactorRequest {

    @NotBlank(message = "Temp token is required")
    @Schema(description = "Temporary token from login response", example = "b55a6f1c-4272-b737-506ac5d6074a", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tempToken;

    @NotBlank(message = "TOTP code is required")
    @Schema(description = "6-digit TOTP code from user's authenticator app", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String totpCode;
}
