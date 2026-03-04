package com.orthopedic.api.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiControlFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    public ApiControlFilter(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (redisTemplate == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIP(request);

        // 1. Maintenance Mode
        String maintenanceMode = redisTemplate.opsForValue().get("maintenance:mode");
        if ("true".equals(maintenanceMode) && !isIpAllowedForMaintenance(clientIp)) {
            // Except health-check
            if (!"/api/v1/public/health-check".equals(path) && !path.startsWith("/api/v1/admin")) {
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"MAINTENANCE\",\"message\":\"System is under maintenance\"}");
                return;
            }
        }

        // 2. Check IP blocklist
        Boolean isBlocked = redisTemplate.opsForSet().isMember("blocked:ips", clientIp);
        if (Boolean.TRUE.equals(isBlocked)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"IP_BLOCKED\",\"message\":\"Access denied\"}");
            return;
        }

        // 3. Check endpoint enabled
        String disabledReason = redisTemplate.opsForValue().get("api:disabled:" + method + ":" + path);
        if (disabledReason != null) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"API_DISABLED\",\"message\":\"" + disabledReason + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isIpAllowedForMaintenance(String ip) {
        if (redisTemplate == null)
            return false;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("maintenance:allowedIps", ip));
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
