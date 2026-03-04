package com.orthopedic.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Local development configuration — active only with
 * -Dspring.profiles.active=local.
 *
 * Provides a {@link RedisTemplate} bean backed by a lazy connection factory so
 * that
 * the many services that inject {@code RedisTemplate<String, Object>} can be
 * constructed without a real Redis server. At runtime calls to the template
 * will
 * fail fast (connection refused), but the application will start and serve HTTP
 * traffic normally. All caching falls back to Spring's simple in-memory cache
 * (configured in application-local.yaml).
 */
@Configuration
@EnableCaching
@Profile("local")
public class LocalDevConfig {

    /**
     * Lettuce connection factory pointing at localhost:6379.
     * The connection is created lazily, so startup succeeds even if Redis is down.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory("localhost", 6379);
        factory.setValidateConnection(false); // Don't fail on startup if Redis is absent
        return factory;
    }

    /**
     * RedisTemplate bean required by TokenService, AuthServiceImpl,
     * TwoFactorServiceImpl, AppointmentServiceImpl, JwtTokenProvider,
     * OAuth2AuthenticationSuccessHandler etc.
     *
     * Actual Redis operations at runtime will throw a connection error
     * (acceptable for local dev since caching is already disabled via
     * spring.cache.type=simple and token blacklist checks are best-effort).
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        return template;
    }
}
