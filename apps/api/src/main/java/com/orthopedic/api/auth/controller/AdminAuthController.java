package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "Admin Authentication", description = "Endpoints for Admin authentication and session management")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    @Operation(summary = "Admin login step 1", description = "Initiate admin login, returns session token for MFA step")
    public ResponseEntity<AdminLoginResponse> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        return ResponseEntity.ok(adminAuthService.adminLogin(request, ipAddress, userAgent));
    }

    @PostMapping("/login/mfa")
    @Operation(summary = "Admin login step 2 (MFA)", description = "Verify MFA code and issue JWT tokens")
    public ResponseEntity<AdminMfaResponse> verifyMfa(
            @Valid @RequestBody AdminMfaRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        return ResponseEntity.ok(adminAuthService.adminMfaVerify(request, ipAddress, userAgent));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get a new access token using a valid refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(adminAuthService.refreshAdminToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout admin", description = "Invalidate tokens and end the session")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest,
            @Valid @RequestBody RefreshTokenRequest request) {
        String authHeader = httpRequest.getHeader("Authorization");
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        adminAuthService.adminLogout(accessToken, request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
