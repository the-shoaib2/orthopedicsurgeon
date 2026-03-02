package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login, register, and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, 
                                             HttpServletRequest servletRequest,
                                             HttpServletResponse servletResponse) {
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        LoginResponse response = authService.login(request, ip, userAgent);
        
        if (response.getRefreshToken() != null) {
            addRefreshTokenCookie(servletResponse, response.getRefreshToken());
            response.setRefreshToken(null); 
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user (patient by default)")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<TokenResponse> refresh(@CookieValue(name = "refreshToken") String refreshToken,
                                               HttpServletResponse servletResponse) {
        TokenResponse response = authService.refreshToken(refreshToken);
        addRefreshTokenCookie(servletResponse, response.getRefreshToken());
        response.setRefreshToken(null);
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
            response.setRefreshToken(null);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate tokens")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader, 
                                     @CookieValue(name = "refreshToken") String refreshToken,
                                     HttpServletResponse servletResponse) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7), refreshToken);
        }
        clearRefreshTokenCookie(servletResponse);
        return ResponseEntity.noContent().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Should be true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
