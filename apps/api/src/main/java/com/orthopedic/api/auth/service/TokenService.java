package com.orthopedic.api.auth.service;

import com.orthopedic.api.config.JwtConfig;
import com.orthopedic.api.auth.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtConfig jwtConfig;

    public TokenService(RedisTemplate<String, Object> redisTemplate, JwtConfig jwtConfig) {
        this.redisTemplate = redisTemplate;
        this.jwtConfig = jwtConfig;
    }

    public String generateAndSaveRefreshToken(User user, String deviceInfo) {
        String token = UUID.randomUUID().toString();
        // ⚡ PERF: Store in Redis with TTL to save Postgres INSERT and manage expiry
        // automatically
        redisTemplate.opsForValue().set(
                "refresh_token:" + token,
                user.getEmail(),
                jwtConfig.getRefreshTokenExpiry(),
                TimeUnit.SECONDS);
        return token;
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete("refresh_token:" + token);
    }

    public String getEmailFromToken(String token) {
        return (String) redisTemplate.opsForValue().get("refresh_token:" + token);
    }
}
