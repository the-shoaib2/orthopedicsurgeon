package com.orthopedic.api.auth.security;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final JwtTokenProvider tokenProvider;
    private final JwtConfig jwtConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider,
            JwtConfig jwtConfig,
            RedisTemplate<String, Object> redisTemplate,
            UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.jwtConfig = jwtConfig;
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String targetUrl;

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN'"));

        if (isAdmin && user.isUsing2fa()) {
            String tempToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("temp_auth:" + tempToken, user.getEmail(), 10, TimeUnit.MINUTES);

            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:4200/auth/2fa")
                    .fragment("tempToken=" + tempToken)
                    .build().toUriString();
        } else {
            String accessToken = tokenProvider.generateAccessToken(userDetails);
            String baseUrl = isAdmin ? "http://localhost:4200" : "http://localhost:4201";
            targetUrl = UriComponentsBuilder.fromUriString(baseUrl + "/auth/callback")
                    .fragment("token=" + accessToken)
                    .build().toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
