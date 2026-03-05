package com.orthopedic.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.Conditions;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.QueryFilters;
import org.zalando.logbook.json.JsonBodyFilters;
import java.util.Objects;
import java.util.Set;

@Configuration
public class LoggingConfig {

    private static final Set<String> SENSITIVE_PROPERTIES = Set.of("password", "token", "client_secret");

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(Conditions.exclude(
                        Conditions.requestTo("/health"),
                        Conditions.requestTo("/metrics"),
                        Conditions.requestTo("/prometheus"),
                        Conditions.requestTo("/actuator/**"),
                        Conditions.requestTo("/swagger-ui/**"),
                        Conditions.requestTo("/v3/api-docs/**"),
                        Conditions.contentType("application/octet-stream"),
                        Conditions.contentType("image/**")))
                .headerFilter(Objects.requireNonNull(HeaderFilters.replaceHeaders("Authorization", "Bearer *******")))
                .headerFilter(Objects.requireNonNull(HeaderFilters.replaceHeaders("X-API-KEY", "*******")))
                .queryFilter(Objects.requireNonNull(QueryFilters.replaceQuery("password", "*******")))
                .queryFilter(Objects.requireNonNull(QueryFilters.replaceQuery("token", "*******")))
                .bodyFilter(Objects
                        .requireNonNull(JsonBodyFilters
                                .replaceJsonStringProperty(Objects.requireNonNull(SENSITIVE_PROPERTIES), "*******")))
                .build();
    }

}
