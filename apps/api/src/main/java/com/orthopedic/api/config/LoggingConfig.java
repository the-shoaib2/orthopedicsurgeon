package com.orthopedic.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Sink;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.Conditions;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.QueryFilters;
import org.zalando.logbook.json.JsonBodyFilters;
import javax.annotation.Nonnull;
import java.util.Objects;

import java.io.IOException;
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
                .sink(new NextJsStyleSink())
                .build();
    }

    private static class NextJsStyleSink implements Sink {
        private final DefaultHttpLogWriter writer = new DefaultHttpLogWriter();

        @Override
        public void write(@Nonnull Precorrelation precorrelation, @Nonnull HttpRequest request) throws IOException {
            // We only log on response to get the status code and duration
        }

        @Override
        public void write(@Nonnull Correlation correlation, @Nonnull HttpRequest request,
                @Nonnull HttpResponse response) throws IOException {
            String method = request.getMethod();
            String path = request.getPath();
            int status = response.getStatus();
            long duration = correlation.getDuration().toMillis();

            String message = String.format("%s %s %d %dms", method, path, status, duration);
            writer.write(correlation, Objects.requireNonNull(message));
        }
    }
}
