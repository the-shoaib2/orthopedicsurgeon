package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.entity.PasswordResetToken;
import com.orthopedic.api.auth.entity.Role;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.exception.InvalidCredentialsException;
import com.orthopedic.api.auth.repository.PasswordResetTokenRepository;
import com.orthopedic.api.auth.repository.RoleRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.security.CustomUserDetailsService;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.orthopedic.api.shared.service.EmailService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JwtConfig jwtConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TwoFactorService twoFactorService;
    private final AuditService auditService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    public AuthServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            JwtConfig jwtConfig,
            RedisTemplate<String, Object> redisTemplate,
            TwoFactorService twoFactorService,
            AuditService auditService,
            CustomUserDetailsService userDetailsService,
            TokenService tokenService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtConfig = jwtConfig;
        this.redisTemplate = redisTemplate;
        this.twoFactorService = twoFactorService;
        this.auditService = auditService;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        // Load user directly from DB — single call, no exception-swallowing
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // 🔒 SECURITY: Check if account is locked
        if (user.isLocked()) {
            auditService.logAudit(user, ipAddress, userAgent, "LOCKED_OUT");
            throw new AuthException("Account is temporarily locked. Please try again later.");
        }

        if (!user.isEnabled()) {
            throw new AuthException("Account is disabled. Please contact support.");
        }

        // ⚡ PERF: Redis-based failed attempt tracking (Near-zero DB overhead)
        String attemptKey = "login_attempts:" + request.getEmail();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Long attempts = redisTemplate.opsForValue().increment(attemptKey);
            redisTemplate.expire(attemptKey, 30, TimeUnit.MINUTES);

            if (attempts != null && attempts >= 5) {
                user.lock(30);
                userRepository.save(user);
                auditService.logAudit(user, ipAddress, userAgent, "ACCOUNT_LOCKED");
            } else {
                auditService.logAudit(user, ipAddress, userAgent, "FAILURE");
            }
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // 🔓 SUCCESS: Clear Redis attempts and update last login (Async)
        redisTemplate.delete(attemptKey);
        updateLoginMetadataAsync(user);

        // 🔒 SECURITY: Check if 2FA is required. Mandatory for
        // ROLE_ADMIN/ROLE_SUPER_ADMIN
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN") || r.getName().equals("ROLE_SUPER_ADMIN")
                        || r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"));

        if (isAdmin || user.isUsing2fa()) {
            String tempToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("temp_auth:" + tempToken, user.getEmail(), 10, TimeUnit.MINUTES);

            auditService.logAudit(user, ipAddress, userAgent, isAdmin ? "2FA_MANDATORY_ADMIN" : "2FA_PENDING");
            return LoginResponse.builder()
                    .requiresTwoFactor(true)
                    .tempToken(tempToken)
                    .build();
        }

        // Build UserDetails from the loaded user (no extra DB call)
        UserDetails userDetails = new com.orthopedic.api.auth.security.CustomUserDetails(user);
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshTokenString = tokenService.generateAndSaveRefreshToken(user, userAgent);

        auditService.logAudit(user, ipAddress, userAgent, "SUCCESS");

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiry())
                .requiresTwoFactor(false)
                .build();
    }

    @org.springframework.scheduling.annotation.Async
    @Transactional
    public void updateLoginMetadataAsync(User user) {
        // Reload in a fresh Hibernate session to avoid detached entity issues
        userRepository.findById(user.getId()).ifPresent(freshUser -> {
            freshUser.setLastLoginAt(java.time.LocalDateTime.now());
            freshUser.resetFailedAttempts();
            userRepository.save(freshUser);
        });
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
        // 🔒 SECURITY: Redis-backed refresh tokens (ultra-fast)
        String email = (String) redisTemplate.opsForValue().get("refresh_token:" + refreshTokenString);
        if (email == null) {
            throw new AuthException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        UserDetails userDetails = new com.orthopedic.api.auth.security.CustomUserDetails(user);
        String newAccessToken = tokenProvider.generateAccessToken(userDetails);

        // Rotate token: delete old, create new
        tokenService.deleteRefreshToken(refreshTokenString);
        String newRefreshTokenString = tokenService.generateAndSaveRefreshToken(user, "rotated");

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

        // Revoke refresh token from Redis
        if (refreshTokenString != null) {
            tokenService.deleteRefreshToken(refreshTokenString);
        }
    }

    @Override
    @Transactional
    public TokenResponse verify2fa(Verify2faRequest request, String userAgent) {
        return twoFactorService.verifyTotpLogin(request.getTempToken(), request.getTotpCode(), userAgent);
    }

    @Override
    @Transactional
    public LoginResponse googleLogin(GoogleLoginRequest request, String ipAddress, String userAgent) {
        if (googleClientId == null || googleClientId.isEmpty()) {
            throw new AuthException("Google authentication is not configured on the server");
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid Google ID token");
        }

        if (idToken == null) {
            throw new InvalidCredentialsException("Invalid Google ID token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();

        if (!payload.getEmailVerified()) {
            throw new AuthException("Google email is not verified");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Auto-register the user if they don't exist
            user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password
            user.setFirstName((String) payload.get("given_name"));
            user.setLastName((String) payload.get("family_name"));
            user.setEnabled(true);

            Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                    .orElseThrow(() -> new AuthException("Default role not found"));
            user.setRoles(Set.of(patientRole));

            user = userRepository.save(user);
            auditService.logAudit(user, ipAddress, userAgent, "GOOGLE_REGISTERED");
        } else {
            if (user.isLocked()) {
                auditService.logAudit(user, ipAddress, userAgent, "GOOGLE_LOGIN_LOCKED_OUT");
                throw new AuthException("Account is temporarily locked. Please try again later.");
            }
            if (!user.isEnabled()) {
                throw new AuthException("Account is disabled. Please contact support.");
            }
        }

        updateLoginMetadataAsync(user);

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN") || r.getName().equals("ROLE_SUPER_ADMIN"));

        if (isAdmin || user.isUsing2fa()) {
            String tempToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("temp_auth:" + tempToken, user.getEmail(), 10, TimeUnit.MINUTES);
            auditService.logAudit(user, ipAddress, userAgent,
                    isAdmin ? "2FA_MANDATORY_ADMIN_GOOGLE" : "2FA_PENDING_GOOGLE");
            return LoginResponse.builder()
                    .requiresTwoFactor(true)
                    .tempToken(tempToken)
                    .build();
        }

        UserDetails userDetails = new com.orthopedic.api.auth.security.CustomUserDetails(user);
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshTokenString = tokenService.generateAndSaveRefreshToken(user, userAgent);

        auditService.logAudit(user, ipAddress, userAgent, "GOOGLE_SUCCESS");

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiry())
                .requiresTwoFactor(false)
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            // Silently return to prevent email enumeration
            return;
        }

        // Remove old tokens
        passwordResetTokenRepository.deleteByUser(user);

        // Ensure changes are flushed before creating the new token, to avoid constraint
        // violations
        passwordResetTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Valid for 1 hour

        passwordResetTokenRepository.save(resetToken);

        // 📧 EMAIL: Send reset link
        String resetUrl = "http://localhost:4200/auth/reset-password?token=" + token;
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("name", user.getFirstName());
        variables.put("resetUrl", resetUrl);
        variables.put("token", token);

        emailService.sendHtmlEmail(user.getEmail(), "Password Reset Request", "password-reset", variables);

        log.info("Password reset token generated and sent for: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request, String ipAddress, String userAgent) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new AuthException("Invalid or expired password reset token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new AuthException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());

        // Also unlock the user in case they were locked out due to too many failed
        // attempts
        user.resetFailedAttempts();
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
        auditService.logAudit(user, ipAddress, userAgent, "PASSWORD_RESET_SUCCESS");
    }
}
