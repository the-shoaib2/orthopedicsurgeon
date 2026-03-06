package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.AdminSessionDto;
import com.orthopedic.api.auth.dto.SessionDto;
import com.orthopedic.api.auth.entity.Session;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.repository.SessionRepository;
import com.orthopedic.api.auth.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final JwtConfig jwtConfig;

    @Override
    public List<AdminSessionDto> getAllActiveSessions() {
        return sessionRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::mapToAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void forceLogout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

        List<Session> activeSessions = sessionRepository.findByUserIdAndIsActiveTrue(user.getId());
        activeSessions.forEach(session -> {
            session.setActive(false);
            session.setTerminatedAt(LocalDateTime.now());
            session.setTerminatedReason("FORCE_LOGOUT_BY_ADMIN");
            sessionRepository.save(session);
            tokenProvider.blacklistToken(session.getAccessTokenJti().toString(), jwtConfig.getAccessTokenExpiry());
        });
    }

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

    @Override
    public List<SessionDto> getLoginHistory(User user) {
        return sessionRepository.findByUser(user)
                .stream()
                .map(session -> mapToDto(session, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearLoginHistory(User user) {
        List<Session> allSessions = sessionRepository.findByUser(user);
        List<Session> inactiveSessions = allSessions.stream()
                .filter(s -> !s.isActive())
                .collect(Collectors.toList());
        
        sessionRepository.deleteAll(inactiveSessions);
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

    private AdminSessionDto mapToAdminDto(Session session) {
        User user = session.getUser();
        return AdminSessionDto.builder()
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
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .userRole(user.getRoles().stream().findFirst().map(r -> r.getName().toString()).orElse("USER"))
                .build();
    }
}
