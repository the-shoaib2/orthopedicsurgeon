package com.orthopedic.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMfaRequest {

    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @NotBlank(message = "TOTP code or backup code is required")
    private String code; // can be totpCode or backupCode

    private String deviceFingerprint;
}
