package com.orthopedic.api.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A clean, Next.js-style API request logger that shows method, path, status,
 * and latency.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    // 🎨 ANSI Color Codes (Bold/Bright versions for better visibility)
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[1;32m";
    private static final String YELLOW = "\u001B[1;33m";
    private static final String RED = "\u001B[1;31m";
    private static final String BLUE = "\u001B[1;34m";
    private static final String PURPLE = "\u001B[1;35m";
    private static final String CYAN = "\u001B[1;36m";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String path = request.getRequestURI();
        String method = request.getMethod();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            // 🎨 Color Logic
            String methodColor = switch (method) {
                case "GET" -> GREEN;
                case "POST" -> YELLOW;
                case "PUT", "PATCH" -> BLUE;
                case "DELETE" -> RED;
                default -> CYAN;
            };

            String statusColor = switch (status) {
                case 401, 403 -> PURPLE; // Auth errors are Purple
                case 400, 404, 409 -> YELLOW; // Client errors are Yellow
                default -> {
                    if (status >= 200 && status < 300)
                        yield GREEN;
                    if (status >= 500)
                        yield RED;
                    yield CYAN;
                }
            };

            // User requested red if 500-800ms or higher
            String durationColor = (duration >= 500) ? RED : (duration >= 200 ? YELLOW : GREEN);

            // Format: ✓ POST /api/v1/auth/login 401 255ms
            String symbol = (status >= 400) ? RED + " ⨯ " : GREEN + " ✓ ";

            log.info("{}{} {}{} " + RESET + "{}{} " + RESET + "{}{}ms{}",
                    symbol, methodColor + BOLD, method, RESET,
                    path,
                    statusColor + BOLD, status,
                    durationColor, duration, RESET);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip noisy actuator endpoints unless they are errors
        return path.contains("/actuator") || path.contains("/favicon.ico");
    }
}
