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
@Schema(description = "Request object for confirming TOTP setup")
public class TwoFactorConfirmRequest {
    @NotBlank(message = "Verification code is required")
    @Schema(description = "6-digit TOTP code from user's authenticator app", example = "123456")
    private String code;
}
