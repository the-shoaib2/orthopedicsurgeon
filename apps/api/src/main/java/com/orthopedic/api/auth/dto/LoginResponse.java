package com.orthopedic.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object containing authentication tokens and status")
public class LoginResponse {

    @Schema(description = "JWT Access Token for authorized requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT Refresh Token (only if not using cookies)", example = "d22e0e8e-6e2b-4e8e-9d8e-7e9b1e2c3d4f")
    private String refreshToken;

    @Schema(description = "Type of the token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiry time in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "Flag indicating if MFA verification is required", example = "false")
    private boolean requiresMfa;

    @Schema(description = "Temporary token for MFA verification challenge", example = "b55a6f1c-4272-b737-506ac5d6074a")
    private String tempToken;

    @Schema(description = "User's unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private java.util.UUID userId;

    @Schema(description = "User's assigned roles", example = "[\"ROLE_PATIENT\"]")
    private java.util.List<String> roles;
}
