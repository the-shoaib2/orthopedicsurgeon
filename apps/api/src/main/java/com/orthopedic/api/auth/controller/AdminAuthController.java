package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "Admin Authentication", description = "Endpoints for Admin authentication and session management")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Value("${app.auth.cookie-name.refresh:refreshToken}")
    private String refreshTokenCookieName;

    @Value("${app.auth.cookie-name.access:accessToken}")
    private String accessTokenCookieName;

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
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String ipAddress = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AdminMfaResponse response = adminAuthService.adminMfaVerify(request, ipAddress, userAgent);

        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(httpResponse, response.getRefreshToken());
        }
        if (response.getAccessToken() != null) {
            addAccessTokenCookie(httpResponse, response.getAccessToken());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get a new access token using a valid refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request,
            HttpServletResponse httpResponse) {
        TokenResponse response = adminAuthService.refreshAdminToken(request.getRefreshToken());

        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(httpResponse, response.getRefreshToken());
        }
        if (response.getAccessToken() != null) {
            addAccessTokenCookie(httpResponse, response.getAccessToken());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout admin", description = "Invalidate tokens and end the session")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @Valid @RequestBody RefreshTokenRequest request) {
        String authHeader = httpRequest.getHeader("Authorization");
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        adminAuthService.adminLogout(accessToken, request.getRefreshToken());
        clearAllCookies(httpResponse);
        return ResponseEntity.ok().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(accessTokenCookieName, accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60); // 15 mins (match policy)
        response.addCookie(cookie);
    }

    private void clearAllCookies(HttpServletResponse response) {
        clearCookie(response, refreshTokenCookieName);
        clearCookie(response, accessTokenCookieName);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
