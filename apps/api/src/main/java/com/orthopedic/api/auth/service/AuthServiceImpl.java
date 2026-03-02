package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.*;
import com.orthopedic.api.auth.entity.Role;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.exception.InvalidCredentialsException;
import com.orthopedic.api.auth.repository.RoleRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import com.orthopedic.api.config.JwtConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
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

    public AuthServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            JwtConfig jwtConfig,
            RedisTemplate<String, Object> redisTemplate,
            TwoFactorService twoFactorService,
            AuditService auditService,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtConfig = jwtConfig;
        this.redisTemplate = redisTemplate;
        this.twoFactorService = twoFactorService;
        this.auditService = auditService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
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

        // ⚡ PERF: Direct authentication check (avoid redundant DB lookup in
        // UserDetailsService)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 🔒 SECURITY: Increment failed attempts
            user.incrementFailedAttempts();
            if (user.getFailedLoginAttempts() >= 5) {
                user.lock(30); // Lock for 30 minutes
                auditService.logAudit(user, ipAddress, userAgent, "ACCOUNT_LOCKED");
            } else {
                auditService.logAudit(user, ipAddress, userAgent, "FAILURE");
            }
            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // 🔓 SUCCESS: Reset failed attempts ONLY if they were > 0 to save an UPDATE
        // call
        if (user.getFailedLoginAttempts() > 0) {
            user.resetFailedAttempts();
            userRepository.save(user);
        }

        // 🔒 SECURITY: Check if 2FA is required for ADMIN roles
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN") || r.getName().equals("ROLE_SUPER_ADMIN"));

        if (isAdmin && user.isUsing2fa()) {
            String tempToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("temp_auth:" + tempToken, user.getEmail(), 10, TimeUnit.MINUTES);

            auditService.logAudit(user, ipAddress, userAgent, "2FA_PENDING");
            return LoginResponse.builder()
                    .requiresTwoFactor(true)
                    .tempToken(tempToken)
                    .build();
        }

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

}
