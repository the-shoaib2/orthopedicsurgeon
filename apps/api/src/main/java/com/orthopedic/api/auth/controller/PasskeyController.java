package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.service.PasskeyService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/passkey")
@RequiredArgsConstructor
@Tag(name = "Passkey Authentication", description = "Endpoints for WebAuthn/Passkey registration and authentication")
public class PasskeyController {

    private final PasskeyService passkeyService;

    @PostMapping("/register-options")
    @Operation(summary = "Get options for registering a new Passkey")
    public ResponseEntity<?> getRegisterOptions(@CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        PublicKeyCredentialCreationOptions options = passkeyService.startRegistration(currentUser.getEmail());
        return ResponseEntity.ok(options);
    }

    @PostMapping("/register")
    @Operation(summary = "Complete Passkey registration")
    public ResponseEntity<?> finishRegistration(@CurrentUser User currentUser, @RequestBody Map<String, String> body) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        String credentialResponseJson = body.get("credentialResponseJson");
        if (credentialResponseJson == null || credentialResponseJson.isEmpty()) {
            return ResponseEntity.badRequest().body("credentialResponseJson is required");
        }
        RegistrationResult result = passkeyService.finishRegistration(currentUser.getEmail(), credentialResponseJson);
        return ResponseEntity.ok(Map.of("success", true, "keyId", result.getKeyId().getId().getBase64Url()));
    }

    @PostMapping("/authenticate-options")
    @Operation(summary = "Get options for authenticating with a Passkey")
    public ResponseEntity<?> getAuthenticateOptions(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("email is required");
        }
        AssertionRequest options = passkeyService.startAuthentication(email);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Complete Passkey authentication (similar to login)")
    public ResponseEntity<?> finishAuthentication(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String credentialResponseJson = body.get("credentialResponseJson");
        if (email == null || credentialResponseJson == null) {
            return ResponseEntity.badRequest().body("email and credentialResponseJson are required");
        }
        AssertionResult result = passkeyService.finishAuthentication(email, credentialResponseJson);
        return ResponseEntity.ok(Map.of(
                "success", result.isSuccess(),
                "signatureCount", result.getSignatureCount(),
                "message", "Authentication successful. Please use standard token endpoint if needed."));
    }
}
