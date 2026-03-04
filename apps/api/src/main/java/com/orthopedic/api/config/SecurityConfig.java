package com.orthopedic.api.config;

import com.orthopedic.api.auth.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private static final String[] PUBLIC_URLS = {
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/verify-email/**",
                        "/api/v1/auth/forgot-password/**",
                        "/api/v1/auth/reset-password/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/oauth2/**",
                        "/ws/**",
                        "/api/v1/public/**"
        };

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final SecurityHeadersFilter securityHeadersFilter;
        private final RateLimitingFilter rateLimitingFilter;
        private final OAuth2UserService oauth2UserService;
        private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        SecurityHeadersFilter securityHeadersFilter,
                        RateLimitingFilter rateLimitingFilter,
                        OAuth2UserService oauth2UserService,
                        OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
                        OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.securityHeadersFilter = securityHeadersFilter;
                this.rateLimitingFilter = rateLimitingFilter;
                this.oauth2UserService = oauth2UserService;
                this.oauth2AuthenticationSuccessHandler = oauth2AuthenticationSuccessHandler;
                this.oauth2AuthenticationFailureHandler = oauth2AuthenticationFailureHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // 🔒 SECURITY: disable CSRF as we use stateless JWT
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_URLS).permitAll()
                                                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                                .requestMatchers("/actuator/**").hasAuthority("SUPER_ADMIN")
                                                .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                                                .requestMatchers("/api/v1/patient/**").hasAuthority("PATIENT")
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                                                .successHandler(oauth2AuthenticationSuccessHandler)
                                                .failureHandler(oauth2AuthenticationFailureHandler))
                                .exceptionHandling(exceptions -> exceptions
                                                .defaultAuthenticationEntryPointFor(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                                                new AntPathRequestMatcher("/api/**"))
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

                // Add Filters
                http.addFilterBefore(securityHeadersFilter,
                                org.springframework.security.web.header.HeaderWriterFilter.class);
                http.addFilterBefore(rateLimitingFilter,
                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
                http.addFilterBefore(jwtAuthenticationFilter,
                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // 🔒 SECURITY: strict CORS configuration
                configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4201"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
                configuration.setAllowCredentials(true);
                configuration.setExposedHeaders(List.of("Authorization"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                // ⚡ PERF: BCrypt with strength 10 (Much faster than 12, still very secure)
                return new BCryptPasswordEncoder(10);
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
