package com.orthopedic.api.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RedisRateLimiter redisRateLimiter;

    public RateLimitingFilter(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIP(request);

        // Define limits based on route
        int limit = 60; // default
        long window = 60; // 1 min

        if (path.startsWith("/api/v1/auth/login")) {
            limit = 5;
            window = 15 * 60; // 15 mins
        } else if (path.startsWith("/api/v1/public/search")) {
            limit = 20;
            window = 60;
        }

        String key = "ratelimit:" + clientIp + ":" + path;

        if (redisRateLimiter.isAllowed(key, limit, window)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("Retry-After", String.valueOf(window));
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
