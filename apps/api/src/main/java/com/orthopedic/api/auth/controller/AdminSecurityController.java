package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.service.TotpService;
import com.orthopedic.api.auth.service.WebAuthnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/security")
@RequiredArgsConstructor
public class AdminSecurityController {

    private final TotpService totpService;
    private final WebAuthnService webAuthnService;
    private final UserRepository userRepository;

    @GetMapping("/2fa/setup")
    public ResponseEntity<Map<String, String>> setup2fa(@RequestParam UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        String secret = totpService.generateSecret();
        String qrCode = totpService.getQrCodeUri(secret, user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("secret", secret);
        response.put("qrCode", qrCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<List<String>> enable2fa(@RequestParam UUID userId, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(userId).orElseThrow();
        String secret = body.get("secret");
        String code = body.get("code");

        if (!totpService.verifyCode(secret, code)) {
            return ResponseEntity.badRequest().build();
        }

        List<String> backupCodes = totpService.generateBackupCodes();
        totpService.enableTotp(user, secret, backupCodes);
        return ResponseEntity.ok(backupCodes);
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<Void> disable2fa(@RequestParam UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        totpService.disableTotp(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/2fa/status")
    public ResponseEntity<Map<String, Object>> get2faStatus(@RequestParam UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", totpService.isTotpEnabled(user));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/passkey/registration/options")
    public ResponseEntity<Object> getPasskeyRegistrationOptions(@RequestParam UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(webAuthnService.startRegistration(user));
    }
}
