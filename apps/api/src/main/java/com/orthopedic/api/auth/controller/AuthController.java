package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.auth.service.AuthService;
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
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Public Authentication and Session Management APIs")
public class AuthController {

    private final AuthService authService;

    @Value("${app.auth.cookie-name:refreshToken}")
    private String cookieName;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Spec A-01: Primary login endpoint for patients and staff.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        LoginResponse response = authService.login(request, ip, userAgent);

        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/mfa-verify")
    @Operation(summary = "Verify TOTP code for MFA", description = "Spec A-02: Verification path for 2FA/MFA challenges.")
    public ResponseEntity<TokenResponse> verifyMfa(@Valid @RequestBody Verify2faRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String userAgent = servletRequest.getHeader("User-Agent");
        TokenResponse response = authService.verify2fa(request, userAgent);
        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile", description = "Spec A-12: Identifies current authenticated user profile from token.")
    public ResponseEntity<User> getCurrentUser(@CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/register/patient")
    @Operation(summary = "Register a new patient", description = "Spec A-07: Public registration for patients.")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address", description = "Spec A-05: Completes registration via email token.")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Spec A-06: Triggers a new verification email.")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-email")
    @Operation(summary = "Check if email exists", description = "Spec A-09: Checks for existing registration without revealing full identity.")
    public ResponseEntity<CheckEmailResponse> checkEmail(@Valid @RequestBody CheckEmailRequest request) {
        return ResponseEntity.ok(authService.checkEmail(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset link", description = "Spec A-10: Triggers password reset flow.")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/{token}")
    @Operation(summary = "Reset password using a token", description = "Spec A-11: Completes password reset flow.")
    public ResponseEntity<Void> resetPassword(@PathVariable String token,
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest servletRequest) {
        request.setToken(token);
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        authService.resetPassword(request, ip, userAgent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Spec A-13: Rotates access and refresh tokens.")
    public ResponseEntity<TokenResponse> refreshToken(
            @CookieValue(name = "${app.auth.cookie-name:refreshToken}", required = false) String refreshToken,
            HttpServletResponse servletResponse) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }
        TokenResponse response = authService.refreshToken(refreshToken);
        addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Spec A-14: Revokes current session and blacklists token.")
    public ResponseEntity<Void> logout(HttpServletRequest request,
            @CookieValue(name = "${app.auth.cookie-name:refreshToken}", required = false) String refreshToken,
            HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            authService.logout(accessToken, refreshToken);
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Spec A-20: Global session revocation.")
    public ResponseEntity<Void> logoutAll(@CurrentUser User user, HttpServletRequest request,
            HttpServletResponse response) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        authService.logoutAll(ip, userAgent, user.getEmail());
        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/token/verify")
    @Operation(summary = "Verify if token is valid", description = "Spec A-14 (extended): Check token validity without profile fetch.")
    public ResponseEntity<Boolean> verifyToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyTokenByString(token));
    }

    @PostMapping("/google")
    @Operation(summary = "Login with Google OAuth2", description = "Spec A-08: SSO path for patients.")
    public ResponseEntity<LoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        LoginResponse response = authService.googleLogin(request, ip, userAgent);

        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        }

        return ResponseEntity.ok(response);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
