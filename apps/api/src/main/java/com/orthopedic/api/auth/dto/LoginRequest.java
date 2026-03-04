package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user login")
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's primary email address", example = "shoaib@orthosync.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "shoaib123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Unique device fingerprint for session tracking", example = "df78-gh90-23kj-90lk")
    private String deviceFingerprint;

    @Schema(description = "One-time password from authenticator app (optional for patients)", example = "123456")
    private String totpCode;
}
