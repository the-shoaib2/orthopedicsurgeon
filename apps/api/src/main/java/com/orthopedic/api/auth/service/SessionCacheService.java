package com.orthopedic.api.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public SessionCacheService(@Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String jti, long expirationSeconds) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set("blacklist:" + jti, "true", Duration.ofSeconds(expirationSeconds));
        }
    }

    public boolean isTokenBlacklisted(String jti) {
        if (redisTemplate == null)
            return false;
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }

    public void cacheSession(String sessionId, Object sessionData, long expirationSeconds) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set("session:" + sessionId, sessionData, Duration.ofSeconds(expirationSeconds));
        }
    }

    public Object getCachedSession(String sessionId) {
        if (redisTemplate == null)
            return null;
        return redisTemplate.opsForValue().get("session:" + sessionId);
    }

    public void invalidateSession(String sessionId) {
        if (redisTemplate != null) {
            redisTemplate.delete("session:" + sessionId);
        }
    }
}
