package com.orthopedic.api.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.entity.*;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.exception.InvalidCredentialsException;
import com.orthopedic.api.auth.repository.*;
import com.orthopedic.api.auth.security.CustomUserDetails;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import com.orthopedic.api.shared.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final TokenService tokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final SessionRepository sessionRepository;

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
            TokenService tokenService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            VerificationTokenRepository verificationTokenRepository,
            EmailService emailService,
            SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtConfig = jwtConfig;
        this.redisTemplate = redisTemplate;
        this.twoFactorService = twoFactorService;
        this.auditService = auditService;
        this.tokenService = tokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.isLocked()) {
            auditService.logAudit(user, ipAddress, userAgent, "LOCKED_OUT");
            throw new AuthException("Account is temporarily locked. Please try again later.");
        }

        if (!user.isEnabled()) {
            throw new AuthException("Account is disabled. Please contact support.");
        }

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

        redisTemplate.delete(attemptKey);
        updateLoginMetadataAsync(user);

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN") || r.getName().equals("ROLE_SUPER_ADMIN")
                        || r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"));

        if (isAdmin || user.isUsing2fa()) {
            String tempToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("temp_auth:" + tempToken, user.getEmail(), 10, TimeUnit.MINUTES);

            auditService.logAudit(user, ipAddress, userAgent, isAdmin ? "2FA_MANDATORY_ADMIN" : "2FA_PENDING");
            return LoginResponse.builder()
                    .requiresMfa(true)
                    .tempToken(tempToken)
                    .userId(user.getId())
                    .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                    .build();
        }

        UserDetails userDetails = new CustomUserDetails(user);
        String jti = UUID.randomUUID().toString();
        String accessToken = tokenProvider.generateAccessToken(userDetails, jti);
        String refreshTokenString = tokenService.generateAndSaveRefreshToken(user, userAgent);

        Session session = Session.builder()
                .user(user)
                .accessTokenJti(UUID.fromString(jti))
                .refreshTokenHash(passwordEncoder.encode(refreshTokenString))
                .deviceFingerprint(request.getDeviceFingerprint())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .lastActivity(LocalDateTime.now())
                .isActive(true)
                .build();
        sessionRepository.save(session);

        auditService.logAudit(user, ipAddress, userAgent, "SUCCESS");

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiry())
                .requiresMfa(false)
                .userId(user.getId())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }

    @org.springframework.scheduling.annotation.Async
    @Transactional
    public void updateLoginMetadataAsync(User user) {
        userRepository.findById(user.getId()).ifPresent(freshUser -> {
            freshUser.setLastLoginAt(LocalDateTime.now());
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

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthException("Passwords do not match");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setEnabled(false); // Mandatory verification as per Phase 1 spec

        String roleName = request.getRole() != null ? request.getRole() : "ROLE_PATIENT";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AuthException("Role not found: " + roleName));
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);

        // Generate Verification Token (A-05/A-06)
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = VerificationToken.builder()
                .token(token)
                .user(savedUser)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        verificationTokenRepository.save(vToken);

        // Send Email (A-05)
        String verifyUrl = "http://localhost:4201/auth/verify?token=" + token;
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("name", savedUser.getFirstName());
        variables.put("verifyUrl", verifyUrl);
        emailService.sendHtmlEmail(savedUser.getEmail(), "Verify your email", "email-verification", variables);

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .message("Registration successful. Please check your email to verify your account.")
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        VerificationToken token = verificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new AuthException("Invalid or expired verification token"));

        if (token.isExpired()) {
            throw new AuthException("Token has expired. Please request a new one.");
        }

        User user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.isEnabled()) {
            throw new AuthException("Email is already verified");
        }

        verificationTokenRepository.deleteByUser(user);
        verificationTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        VerificationToken vToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        verificationTokenRepository.save(vToken);

        String verifyUrl = "http://localhost:4201/auth/verify?token=" + token;
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("name", user.getFirstName());
        variables.put("verifyUrl", verifyUrl);
        emailService.sendHtmlEmail(user.getEmail(), "Verify your email", "email-verification", variables);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckEmailResponse checkEmail(CheckEmailRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        String email = request.getEmail();
        String mask = "";
        if (exists) {
            int atIndex = email.indexOf("@");
            String username = email.substring(0, atIndex);
            String domain = email.substring(atIndex + 1);
            mask = username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@"
                    + domain.charAt(0) + "***" + domain.substring(domain.indexOf("."));
        }
        return CheckEmailResponse.builder().exists(exists).mask(mask).build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyTokenByString(String token) {
        return tokenProvider.validateToken(token);
    }

    @Override
    @Transactional
    public void logoutAll(String ipAddress, String userAgent, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        sessionRepository.findByUser(user).stream()
                .filter(Session::isActive)
                .forEach(session -> {
                    session.setActive(false);
                    session.setTerminatedAt(LocalDateTime.now());
                    session.setTerminatedReason("ADMIN_GLOBAL_LOGOUT");
                    sessionRepository.save(session);
                    tokenProvider.blacklistToken(session.getAccessTokenJti().toString(),
                            jwtConfig.getAccessTokenExpiry());
                });

        auditService.logAudit(user, ipAddress, userAgent, "GLOBAL_LOGOUT");
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshTokenString) {
        String email = tokenService.getEmailFromToken(refreshTokenString);
        if (email == null) {
            throw new AuthException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        UserDetails userDetails = new CustomUserDetails(user);
        String newJti = UUID.randomUUID().toString();
        String newAccessToken = tokenProvider.generateAccessToken(userDetails, newJti);

        tokenService.deleteRefreshToken(refreshTokenString);
        String newRefreshTokenString = tokenService.generateAndSaveRefreshToken(user, "rotated");

        Session session = Session.builder()
                .user(user)
                .accessTokenJti(UUID.fromString(newJti))
                .refreshTokenHash(passwordEncoder.encode(newRefreshTokenString))
                .lastActivity(LocalDateTime.now())
                .isActive(true)
                .build();
        sessionRepository.save(session);

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
        String jti = tokenProvider.getJtiFromToken(accessToken);
        if (jti != null) {
            tokenProvider.blacklistToken(jti, jwtConfig.getAccessTokenExpiry());
            sessionRepository.findByAccessTokenJti(UUID.fromString(jti)).ifPresent(session -> {
                session.setActive(false);
                session.setTerminatedAt(LocalDateTime.now());
                session.setTerminatedReason("USER_LOGOUT");
                sessionRepository.save(session);
            });
        }

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

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new AuthException("Google email is not verified");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
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
                    .requiresMfa(true)
                    .tempToken(tempToken)
                    .userId(user.getId())
                    .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                    .build();
        }

        UserDetails userDetails = new CustomUserDetails(user);
        String jti = UUID.randomUUID().toString();
        String accessToken = tokenProvider.generateAccessToken(userDetails, jti);
        String refreshTokenString = tokenService.generateAndSaveRefreshToken(user, userAgent);

        Session session = Session.builder()
                .user(user)
                .accessTokenJti(UUID.fromString(jti))
                .refreshTokenHash(passwordEncoder.encode(refreshTokenString))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .lastActivity(LocalDateTime.now())
                .isActive(true)
                .build();
        sessionRepository.save(session);

        auditService.logAudit(user, ipAddress, userAgent, "GOOGLE_SUCCESS");

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiry())
                .requiresMfa(false)
                .userId(user.getId())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return;
        }

        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);

        String resetUrl = "http://localhost:4200/auth/reset-password?token=" + token;
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("name", user.getFirstName());
        variables.put("resetUrl", resetUrl);
        variables.put("token", token);

        emailService.sendHtmlEmail(user.getEmail(), "Password Reset Request", "password-reset", variables);
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
        user.resetFailedAttempts();
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
        auditService.logAudit(user, ipAddress, userAgent, "PASSWORD_RESET_SUCCESS");
    }
}
