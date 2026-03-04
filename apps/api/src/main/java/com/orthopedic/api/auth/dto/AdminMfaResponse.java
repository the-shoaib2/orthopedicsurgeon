package com.orthopedic.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMfaResponse {
    private String accessToken;
    private String refreshToken;
    private boolean deviceTrusted;
    private UUID sessionId;
    private UUID userId;
}
