package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for login, register, and token management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        LoginResponse response = authService.login(request, ip, userAgent);

        // Also set refresh token in HttpOnly cookie for browser clients
        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(servletResponse, response.getRefreshToken());
            // Keep refresh token in body so API clients (Postman/mobile) can use it
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user (patient by default)")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login/google")
    @Operation(summary = "Login or Register with Google")
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

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset link")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using a token")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        authService.resetPassword(request, ip, userAgent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token (cookie or request body)")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse servletResponse) {

        // Accept from cookie (browsers) or body (Postman/mobile)
        String refreshToken = cookieRefreshToken;
        if (refreshToken == null && body != null) {
            refreshToken = body.getRefreshToken();
        }
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        TokenResponse response = authService.refreshToken(refreshToken);
        addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "Verify TOTP code for 2FA")
    public ResponseEntity<TokenResponse> verify2fa(@Valid @RequestBody Verify2faRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String userAgent = servletRequest.getHeader("User-Agent");
        TokenResponse response = authService.verify2fa(request, userAgent);
        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate tokens")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse servletResponse) {

        String refreshToken = cookieRefreshToken;
        if (refreshToken == null && body != null) {
            refreshToken = body.getRefreshToken();
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7), refreshToken);
        }
        clearRefreshTokenCookie(servletResponse);
        return ResponseEntity.noContent().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production (HTTPS required)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
