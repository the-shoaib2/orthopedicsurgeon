package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.TwoFactorSetupResponse;
import com.orthopedic.api.auth.entity.TotpSecret;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.entity.TotpSecret;
import com.orthopedic.api.auth.repository.TotpSecretRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TotpSecretRepository totpSecretRepository;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private JwtConfig jwtConfig;
    @Mock
    private SecretGenerator secretGenerator;
    @Mock
    private QrGenerator qrGenerator;
    @Mock
    private CodeVerifier codeVerifier;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TwoFactorServiceImpl twoFactorService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@example.com");
    }

    @Test
    void setupTotp_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(secretGenerator.generate()).thenReturn("SECRET");
        when(qrGenerator.generate(any())).thenReturn(new byte[] { 1, 2, 3 });
        when(qrGenerator.getImageMimeType()).thenReturn("image/png");

        TwoFactorSetupResponse response = twoFactorService.setupTotp(1L);

        assertNotNull(response);
        assertEquals("SECRET", response.getSecretKey());
        assertEquals(8, response.getBackupCodes().size());
        verify(totpSecretRepository).save(any());
    }

    @Test
    void verifyAndEnableTotp_Success() {
        TotpSecret secret = new TotpSecret();
        secret.setSecret("SECRET");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(totpSecretRepository.findByUser(any())).thenReturn(Optional.of(secret));
        when(codeVerifier.isValidCode(anyString(), anyString())).thenReturn(true);

        boolean result = twoFactorService.verifyAndEnableTotp(1L, "123456");

        assertTrue(result);
        assertTrue(testUser.isUsing2fa());
        verify(totpSecretRepository).save(secret);
    }
}
