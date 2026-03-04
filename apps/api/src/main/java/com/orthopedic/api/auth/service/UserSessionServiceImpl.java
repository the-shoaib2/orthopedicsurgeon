package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.SessionDto;
import com.orthopedic.api.auth.entity.Session;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.repository.SessionRepository;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final SessionRepository sessionRepository;
    private final JwtTokenProvider tokenProvider;
    private final JwtConfig jwtConfig;

    @Override
    public List<SessionDto> getActiveSessions(User user, String currentAccessTokenJti) {
        return sessionRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(session -> mapToDto(session, currentAccessTokenJti))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeSession(User user, UUID sessionId) {
        Session session = sessionRepository.findBySessionIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new AuthException("Session not found or does not belong to you"));

        if (!session.isActive()) {
            return;
        }

        session.setActive(false);
        session.setTerminatedAt(LocalDateTime.now());
        session.setTerminatedReason("USER_REVOKED");
        sessionRepository.save(session);

        // Blacklist Access Token
        tokenProvider.blacklistToken(session.getAccessTokenJti().toString(), jwtConfig.getAccessTokenExpiry());
    }

    @Override
    @Transactional
    public void revokeOtherSessions(User user, String currentAccessTokenJti) {
        List<Session> activeSessions = sessionRepository.findByUserIdAndIsActiveTrue(user.getId());

        activeSessions.stream()
                .filter(s -> currentAccessTokenJti == null
                        || !s.getAccessTokenJti().toString().equals(currentAccessTokenJti))
                .forEach(session -> {
                    session.setActive(false);
                    session.setTerminatedAt(LocalDateTime.now());
                    session.setTerminatedReason("USER_REVOKED_OTHER_SESSIONS");
                    sessionRepository.save(session);
                    tokenProvider.blacklistToken(session.getAccessTokenJti().toString(),
                            jwtConfig.getAccessTokenExpiry());
                });
    }

    private SessionDto mapToDto(Session session, String currentAccessTokenJti) {
        return SessionDto.builder()
                .sessionId(session.getSessionId())
                .deviceName(session.getDeviceName())
                .deviceType(session.getDeviceType())
                .browser(session.getBrowser())
                .os(session.getOs())
                .ipAddress(session.getIpAddress())
                .location(session.getLocation())
                .createdAt(session.getCreatedAt())
                .lastActivity(session.getLastActivity())
                .isActive(session.isActive())
                .isCurrentSession(currentAccessTokenJti != null &&
                        session.getAccessTokenJti().toString().equals(currentAccessTokenJti))
                .build();
    }
}
