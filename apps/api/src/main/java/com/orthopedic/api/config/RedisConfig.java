package com.orthopedic.api.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@Profile("!local") // Skip all Redis beans when running without Docker
public class RedisConfig {

        /**
         * MixIn to suppress Hibernate proxy-specific fields that cannot be
         * deserialized outside of an active Hibernate Session.
         */
        @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "fieldHandler" })
        abstract static class HibernateProxyMixin {
        }

        @Bean
        public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                // Use NON_FINAL typing so abstract/interface types get @class stored,
                // but avoid embedding Hibernate-internal collection class names.
                // NON_FINAL is needed for Spring Security types; we handle Hibernate
                // types separately via the MixIn above.
                objectMapper.activateDefaultTyping(
                                LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.NON_FINAL,
                                JsonTypeInfo.As.PROPERTY);

                // Suppress hibernate proxy fields on any Object
                objectMapper.addMixIn(Object.class, HibernateProxyMixin.class);

                return new GenericJackson2JsonRedisSerializer(objectMapper);
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                        GenericJackson2JsonRedisSerializer serializer) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(serializer);
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setHashValueSerializer(serializer);
                return template;
        }

        @Bean
        public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                        GenericJackson2JsonRedisSerializer serializer) {
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(serializer));

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(config.entryTtl(Duration.ofHours(1)))
                                .withCacheConfiguration("home-stats", config.entryTtl(Duration.ofHours(1)))
                                .withCacheConfiguration("featured-doctors", config.entryTtl(Duration.ofHours(1)))
                                .withCacheConfiguration("hero-slides", config.entryTtl(Duration.ofHours(24)))
                                .withCacheConfiguration("testimonials", config.entryTtl(Duration.ofHours(6)))
                                .withCacheConfiguration("hospitals", config.entryTtl(Duration.ofMinutes(30)))
                                .withCacheConfiguration("doctors", config.entryTtl(Duration.ofMinutes(15)))
                                .withCacheConfiguration("doctor_slots", config.entryTtl(Duration.ofMinutes(5)))
                                .build();
        }
}
