package com.orthopedic.api.auth.service.impl;

import com.orthopedic.api.auth.entity.TotpSecret;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.TotpSecretRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.service.TotpService;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TotpServiceImpl implements TotpService {

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;
    private final TotpSecretRepository totpSecretRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String generateSecret() {
        return secretGenerator.generate();
    }

    @Override
    public String getQrCodeUri(String secret, String email) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("Orthopedic Surgeon Platform")
                .algorithm(dev.samstevens.totp.code.HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] imageData = qrGenerator.generate(data);
            return Utils.getDataUriForImage(imageData, qrGenerator.getImageMimeType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    @Override
    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }

    @Override
    public List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codes.add(secretGenerator.generate().substring(0, 8).toUpperCase());
        }
        return codes;
    }

    @Override
    @Transactional
    public void enableTotp(User user, String secret, List<String> backupCodes) {
        TotpSecret totpSecret = totpSecretRepository.findByUser(user)
                .orElse(new TotpSecret());

        totpSecret.setUser(user);
        totpSecret.setSecret(secret); // In a real app, encrypt this additional to DB encryption
        totpSecret.setVerified(true);

        String hashedBackupCodes = backupCodes.stream()
                .map(passwordEncoder::encode)
                .collect(Collectors.joining(","));
        totpSecret.setBackupCodes(hashedBackupCodes);

        totpSecretRepository.save(totpSecret);

        user.setUsing2fa(true);
        userRepository.save(user);
    }

    @Override
    public boolean isTotpEnabled(User user) {
        return user.isUsing2fa();
    }

    @Override
    @Transactional
    public void disableTotp(User user) {
        totpSecretRepository.deleteByUser(user);
        user.setUsing2fa(false);
        userRepository.save(user);
    }

    @Override
    public boolean verifyBackupCode(User user, String code) {
        return totpSecretRepository.findByUser(user)
                .map(totp -> {
                    if (totp.getBackupCodes() == null)
                        return false;
                    List<String> codes = new ArrayList<>(Arrays.asList(totp.getBackupCodes().split(",")));
                    for (int i = 0; i < codes.size(); i++) {
                        if (passwordEncoder.matches(code, codes.get(i))) {
                            codes.remove(i);
                            totp.setBackupCodes(String.join(",", codes));
                            totpSecretRepository.save(totp);
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
    }
}
