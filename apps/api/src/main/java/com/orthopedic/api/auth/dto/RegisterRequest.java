package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for new user registration")
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "Shoaib", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Hasan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's primary email address", example = "shoaib@orthosync.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "User's password (min 8 characters)", example = "shoaib123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Schema(description = "Confirm User's password", example = "shoaib123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;

    @Schema(description = "User's phone number", example = "+8801700000000")
    private String phone;

    @Schema(description = "User's date of birth", example = "1990-01-01")
    private java.time.LocalDate dateOfBirth;

    @Schema(description = "User's gender", example = "MALE")
    private String gender;

    @Schema(description = "Device fingerprint for registration", example = "df78-gh90-23kj-90lk")
    private String deviceFingerprint;

    @Schema(description = "User's role (defaults to ROLE_PATIENT)", example = "ROLE_PATIENT")
    private String role;
}
