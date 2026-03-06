package com.orthopedic.api.security.filter;

import com.orthopedic.api.auth.security.CustomUserDetailsService;
import com.orthopedic.api.auth.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @org.springframework.beans.factory.annotation.Value("${app.auth.cookie-name.access:accessToken}")
    private String accessTokenCookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else {
            token = getCookieValue(request, accessTokenCookieName);
        }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (tokenProvider.validateToken(token)) {
                io.jsonwebtoken.Claims claims = tokenProvider.getClaimsFromToken(token);

                String username = claims.getSubject();

                // Device fingerprint check (simplified for now)
                String storedFingerprint = (String) claims.get("deviceFingerprint");
                String currentFingerprint = getDeviceFingerprint(request);

                if (storedFingerprint != null && !storedFingerprint.equals(currentFingerprint)) {
                    log.warn("⚠️ Device fingerprint mismatch for user: {}", username);
                    // On admin surface, we might want to reject. On public, we just log.
                    if (request.getRequestURI().startsWith("/api/v1/admin")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authentication
                        .setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                                .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

        } catch (Exception e) {
            log.error("❌ JWT Validation failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getDeviceFingerprint(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        String al = request.getHeader("Accept-Language");
        String df = request.getHeader("X-Device-Fingerprint");
        return (ua != null ? ua : "") + "|" + (al != null ? al : "") + "|" + (df != null ? df : "");
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null)
            return null;
        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
