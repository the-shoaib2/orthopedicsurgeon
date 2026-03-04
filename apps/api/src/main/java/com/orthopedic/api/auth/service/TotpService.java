package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.entity.User;
import java.util.List;

public interface TotpService {
    String generateSecret();

    String getQrCodeUri(String secret, String email);

    boolean verifyCode(String secret, String code);

    List<String> generateBackupCodes();

    void enableTotp(User user, String secret, List<String> backupCodes);

    boolean isTotpEnabled(User user);

    void disableTotp(User user);

    boolean verifyBackupCode(User user, String code);
}
