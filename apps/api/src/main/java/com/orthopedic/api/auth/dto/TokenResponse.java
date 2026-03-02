package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing new access and refresh tokens")
public class TokenResponse {

    @Schema(description = "New JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "New JWT Refresh Token", example = "d22e0e8e-6e2b-4e8e-9d8e-7e9b1e2c3d4f")
    private String refreshToken;

    @Schema(description = "Type of the token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiry time in seconds", example = "3600")
    private Long expiresIn;
}
