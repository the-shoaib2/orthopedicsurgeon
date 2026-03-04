package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.SessionDto;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.auth.service.UserSessionService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Management", description = "Endpoints for user session tracking and management")
public class UserSessionController {

    private final UserSessionService sessionService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    @Operation(summary = "Get all active sessions for current user")
    public ResponseEntity<List<SessionDto>> getActiveSessions(
            @CurrentUser User currentUser,
            HttpServletRequest request) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        String jti = extractJti(request);
        return ResponseEntity.ok(sessionService.getActiveSessions(currentUser, jti));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Revoke a specific session")
    public ResponseEntity<Void> revokeSession(
            @CurrentUser User currentUser,
            @PathVariable UUID sessionId) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        sessionService.revokeSession(currentUser, sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/other")
    @Operation(summary = "Revoke all other active sessions except the current one")
    public ResponseEntity<Void> revokeOtherSessions(
            @CurrentUser User currentUser,
            HttpServletRequest request) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        String jti = extractJti(request);
        sessionService.revokeOtherSessions(currentUser, jti);
        return ResponseEntity.noContent().build();
    }

    private String extractJti(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return tokenProvider.getJtiFromToken(token);
        }
        return null;
    }
}
