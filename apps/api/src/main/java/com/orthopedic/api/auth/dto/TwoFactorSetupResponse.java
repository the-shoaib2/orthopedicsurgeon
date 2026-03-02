package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for 2FA setup process")
public class TwoFactorSetupResponse {

    @Schema(description = "URL for the QR code image", example = "otpauth://totp/OrthopedicSurgeon:shoaib@orthosync.com?secret=JBSWY3DPEHPK3PXP&issuer=OrthopedicSurgeon")
    private String qrCodeUrl;

    @Schema(description = "Base32 encoded secret key", example = "JBSWY3DPEHPK3PXP")
    private String secretKey;

    @Schema(description = "List of backup recovery codes", example = "[\"1234-5678\", \"9012-3456\"]")
    private List<String> backupCodes;
}
