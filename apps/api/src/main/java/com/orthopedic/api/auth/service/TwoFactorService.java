package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.TokenResponse;
import com.orthopedic.api.auth.dto.TwoFactorSetupResponse;
import java.util.UUID;

public interface TwoFactorService {
    TwoFactorSetupResponse setupTotp(UUID userId);

    boolean verifyAndEnableTotp(UUID userId, String code);

    TokenResponse verifyTotpLogin(String tempToken, String code, String userAgent);
}
