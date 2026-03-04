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
public class AdminLoginResponse {
    private boolean requiresMfa;
    private String sessionToken; // Temporary token used for the MFA step
    private UUID userId;
}
