package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest request, String ipAddress, String userAgent);
    RegisterResponse register(RegisterRequest request);
    TokenResponse refreshToken(String refreshToken);
    TokenResponse verify2fa(Verify2faRequest request, String userAgent);
    void logout(String accessToken, String refreshToken);
}
