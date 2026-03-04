package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.entity.*;
import com.orthopedic.api.auth.exception.InvalidCredentialsException;
import com.orthopedic.api.auth.repository.*;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private JwtConfig jwtConfig;
    @Mock
    private AuditService auditService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Role patientRole;

    @BeforeEach
    void setUp() {
        patientRole = new Role();
        patientRole.setName("ROLE_PATIENT");

        testUser = new User();
        testUser.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setRoles(Set.of(patientRole));
        testUser.setEnabled(true);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void login_Success_Patient() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenProvider.generateAccessToken(any())).thenReturn("accessToken");
        when(jwtConfig.getAccessTokenExpiry()).thenReturn(900L);
        when(jwtConfig.getRefreshTokenExpiry()).thenReturn(604800L);

        LoginResponse response = authService.login(request, "127.0.0.1", "device");

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertFalse(response.isRequiresTwoFactor());
        verify(auditService).logAudit(any(), any(), any(), eq("SUCCESS"));
    }

    @Test
    void login_Failure_WrongCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request, "127.0.0.1", "device"));
        verify(auditService).logAudit(any(), any(), any(), eq("FAILURE"));
    }

    @Test
    void refreshToken_Success() {
        String token = "validToken";
        when(valueOperations.get("refresh_token:" + token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateAccessToken(any())).thenReturn("newAccessToken");
        when(jwtConfig.getAccessTokenExpiry()).thenReturn(900L);

        TokenResponse response = authService.refreshToken(token);

        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        verify(redisTemplate).delete("refresh_token:" + token);
    }
}
