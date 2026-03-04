package com.orthopedic.api.auth.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
