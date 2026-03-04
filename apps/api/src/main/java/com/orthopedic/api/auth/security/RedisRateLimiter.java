package com.orthopedic.api.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiter(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * sliding window rate limiter
     * 
     * @param key           e.g. "ratelimit:192.168.1.1:/api/v1/auth/login"
     * @param limit         maximum requests allowed in the window
     * @param windowSeconds time window in seconds
     * @return true if allowed, false if limit exceeded
     */
    public boolean isAllowed(String key, int limit, long windowSeconds) {
        if (redisTemplate == null) {
            return true; // Fail-open (or local dev mode) if redis is not configured
        }
        long currentTimeMillis = Instant.now().toEpochMilli();
        long windowStartMillis = currentTimeMillis - (windowSeconds * 1000);

        try {
            redisTemplate.execute((org.springframework.data.redis.connection.RedisConnection connection) -> {
                byte[] keyBytes = key.getBytes();
                connection.zRemRangeByScore(keyBytes, 0, windowStartMillis);
                connection.zAdd(keyBytes, currentTimeMillis, UUID.randomUUID().toString().getBytes());
                connection.expire(keyBytes, windowSeconds);
                return null;
            });
            Long count = redisTemplate.opsForZSet().zCard(key);
            return count != null && count <= limit;
        } catch (Exception e) {
            // fallback to allow on redis failure
            return true;
        }
    }
}
