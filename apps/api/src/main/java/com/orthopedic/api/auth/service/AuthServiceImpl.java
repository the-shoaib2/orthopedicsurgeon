package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.entity.*;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.exception.InvalidCredentialsException;
import com.orthopedic.api.auth.repository.*;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAuditRepository loginAuditRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtConfig jwtConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TwoFactorService twoFactorService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

            // 🔒 SECURITY: Check if 2FA is required for ADMIN roles
            boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN") || r.getName().equals("ROLE_SUPER_ADMIN"));

            if (isAdmin && user.isUsing2fa()) {
                String tempToken = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set("temp_auth:" + tempToken, user.getEmail(), 10, TimeUnit.MINUTES);
                
                logAudit(user, ipAddress, userAgent, "2FA_PENDING");
                return LoginResponse.builder()
                    .requiresTwoFactor(true)
                    .tempToken(tempToken)
                    .build();
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = tokenProvider.generateAccessToken(userDetails);
            String refreshTokenString = generateAndSaveRefreshToken(user, userAgent);

            logAudit(user, ipAddress, userAgent, "SUCCESS");

            return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiry())
                .requiresTwoFactor(false)
                .build();

        } catch (AuthenticationException e) {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> logAudit(user, ipAddress, userAgent, "FAILURE"));
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        // 🔒 SECURITY: Constant-time hashing with BCrypt(12)
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEnabled(true); // Should be false until email verification in production

        String roleName = request.getRole() != null ? request.getRole() : "ROLE_PATIENT";
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new AuthException("Role not found: " + roleName));
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
            .userId(savedUser.getId())
            .email(savedUser.getEmail())
            .message("User registered successfully. Please verify your email.")
            .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshTokenString) {
        // 🔒 SECURITY: Refresh token rotation
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(refreshTokenString)
            .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthException("Refresh token expired or revoked");
        }

        User user = refreshToken.getUser();
        
        // Load UserDetails for access token generation
        UserDetails userDetails = new com.orthopedic.api.auth.security.CustomUserDetails(user);
        String newAccessToken = tokenProvider.generateAccessToken(userDetails);
        
        // Rotate refresh token
        refreshTokenRepository.delete(refreshToken);
        String newRefreshTokenString = generateAndSaveRefreshToken(user, refreshToken.getDeviceInfo());

        return TokenResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshTokenString)
            .tokenType("Bearer")
            .expiresIn(jwtConfig.getAccessTokenExpiry())
            .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshTokenString) {
        // Blacklist access token
        tokenProvider.blacklistToken(accessToken, jwtConfig.getAccessTokenExpiry());
        
        // Revoke refresh token
        refreshTokenRepository.findByTokenHash(refreshTokenString)
            .ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
    }

    @Override
    @Transactional
    public TokenResponse verify2fa(Verify2faRequest request, String userAgent) {
        return twoFactorService.verifyTotpLogin(request.getTempToken(), request.getTotpCode(), userAgent);
    }

    private String generateAndSaveRefreshToken(User user, String deviceInfo) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(token);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiry()));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private void logAudit(User user, String ip, String device, String status) {
        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setIpAddress(ip);
        audit.setDeviceInfo(device);
        audit.setStatus(status);
        loginAuditRepository.save(audit);
    }
}
