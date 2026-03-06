package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.AdminSessionDto;
import com.orthopedic.api.auth.entity.Role;
import com.orthopedic.api.auth.entity.Session;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.SessionRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private UserSessionServiceImpl userSessionService;

    private User testUser;
    private Session testSession;
    private UUID userId;
    private UUID sessionId;
    private UUID jti;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        jti = UUID.randomUUID();

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        testUser = User.builder()
                .id(userId)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .roles(Set.of(adminRole))
                .build();

        testSession = Session.builder()
                .sessionId(sessionId)
                .user(testUser)
                .accessTokenJti(jti)
                .isActive(true)
                .deviceName("Test Dev")
                .build();
    }

    @Test
    void getAllActiveSessions_ShouldReturnDtos() {
        when(sessionRepository.findAllByIsActiveTrue()).thenReturn(List.of(testSession));

        List<AdminSessionDto> result = userSessionService.getAllActiveSessions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sessionId, result.get(0).getSessionId());
        assertEquals("admin@example.com", result.get(0).getUserEmail());
        verify(sessionRepository).findAllByIsActiveTrue();
    }

    @Test
    void forceLogout_ShouldRevokeAllSessions() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(testSession));
        when(jwtConfig.getAccessTokenExpiry()).thenReturn(900L); // 15 mins in seconds

        userSessionService.forceLogout(userId);

        assertFalse(testSession.isActive());
        assertNotNull(testSession.getTerminatedAt());
        assertEquals("FORCE_LOGOUT_BY_ADMIN", testSession.getTerminatedReason());
        verify(sessionRepository).save(testSession);
        verify(tokenProvider).blacklistToken(eq(jti.toString()), anyLong());
    }
}
