package com.orthopedic.api.auth.security;

import com.orthopedic.api.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtConfig jwtConfig;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtTokenProvider(JwtConfig jwtConfig, RedisTemplate<String, Object> redisTemplate) {
        this.jwtConfig = jwtConfig;
        this.redisTemplate = redisTemplate;
    }

    private java.security.Key signingKey;
    private java.security.PublicKey publicKey;
    private boolean useRsa = false;

    @PostConstruct
    public void init() throws Exception {
        if (jwtConfig.getPrivateKeyPath() != null && jwtConfig.getPublicKeyPath() != null) {
            this.signingKey = loadPrivateKey(jwtConfig.getPrivateKeyPath());
            this.publicKey = loadPublicKey(jwtConfig.getPublicKeyPath());
            this.useRsa = true;
        } else if (jwtConfig.getSecret() != null && !jwtConfig.getSecret().isEmpty()) {
            log.info("Using HMAC with provided secret for JWT");
            this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
            this.useRsa = false;
        } else {
            log.warn("JWT keys/secret not provided, generating temporary RSA keys for development");
            var keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
            this.signingKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            this.useRsa = true;
        }
    }

    public String generateAccessToken(UserDetails userDetails, String jti) {
        Map<String, Object> claims = Map.of(
                "roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiry() * 1000));

        if (jti != null) {
            builder.setId(jti);
        }

        return builder.signWith(signingKey, useRsa ? SignatureAlgorithm.RS256 : SignatureAlgorithm.HS256).compact();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails, null);
    }

    public String generateRefreshToken(UUID userId, String jti) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiry() * 1000));

        if (jti != null) {
            builder.setId(jti);
        }

        return builder.signWith(signingKey, useRsa ? SignatureAlgorithm.RS256 : SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(UUID userId) {
        return generateRefreshToken(userId, null);
    }

    public boolean validateToken(String token) {
        try {
            JwtParserBuilder parserBuilder = Jwts.parser();
            if (useRsa) {
                parserBuilder.verifyWith((PublicKey) publicKey);
            } else {
                parserBuilder.verifyWith((javax.crypto.SecretKey) signingKey);
            }
            String jti = parserBuilder.build().parseSignedClaims(token).getPayload().getId();
            if (jti != null && isTokenBlacklisted(jti)) {
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        JwtParserBuilder parserBuilder = Jwts.parser();
        if (useRsa) {
            parserBuilder.verifyWith((PublicKey) publicKey);
        } else {
            parserBuilder.verifyWith((javax.crypto.SecretKey) signingKey);
        }
        return parserBuilder.build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public String getJtiFromToken(String token) {
        try {
            JwtParserBuilder parserBuilder = Jwts.parser();
            if (useRsa) {
                parserBuilder.verifyWith((PublicKey) publicKey);
            } else {
                parserBuilder.verifyWith((javax.crypto.SecretKey) signingKey);
            }
            return parserBuilder.build()
                    .parseSignedClaims(token).getPayload().getId();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isTokenBlacklisted(String jti) {
        if (jti == null)
            return false;
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }

    public void blacklistToken(String jti, long expirySeconds) {
        if (jti != null) {
            redisTemplate.opsForValue().set("blacklist:" + jti, "true", expirySeconds, TimeUnit.SECONDS);
        }
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        String privateKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        String publicKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
