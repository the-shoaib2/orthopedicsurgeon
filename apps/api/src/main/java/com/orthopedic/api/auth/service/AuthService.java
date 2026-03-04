package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest request, String ipAddress, String userAgent);

    RegisterResponse register(RegisterRequest request);

    TokenResponse refreshToken(String refreshTokenString);

    void logout(String accessToken, String refreshTokenString);

    TokenResponse verify2fa(Verify2faRequest request, String userAgent);

    LoginResponse googleLogin(GoogleLoginRequest request, String ipAddress, String userAgent);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request, String ipAddress, String userAgent);

    // Group A - New methods
    void verifyEmail(VerifyEmailRequest request);

    void resendVerification(ResendVerificationRequest request);

    CheckEmailResponse checkEmail(CheckEmailRequest request);

    boolean verifyTokenByString(String token);

    void logoutAll(String ipAddress, String userAgent, String email);
}
