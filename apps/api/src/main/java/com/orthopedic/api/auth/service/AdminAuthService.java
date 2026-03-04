package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.AdminLoginRequest;
import com.orthopedic.api.auth.dto.AdminLoginResponse;
import com.orthopedic.api.auth.dto.AdminMfaRequest;
import com.orthopedic.api.auth.dto.AdminMfaResponse;
import com.orthopedic.api.auth.dto.TokenResponse;

public interface AdminAuthService {
    AdminLoginResponse adminLogin(AdminLoginRequest request, String ipAddress, String userAgent);

    AdminMfaResponse adminMfaVerify(AdminMfaRequest request, String ipAddress, String userAgent);

    TokenResponse refreshAdminToken(String refreshTokenString);

    void adminLogout(String accessToken, String refreshTokenString);
}
