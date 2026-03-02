package com.orthopedic.api.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter appointmentBookedCounter(MeterRegistry registry) {
        return Counter.builder("appointments.booked.total")
            .description("Total number of appointments booked")
            .register(registry);
    }

    @Bean
    public Counter paymentSuccessCounter(MeterRegistry registry) {
        return Counter.builder("payments.success.total")
            .description("Total number of successful payments")
            .register(registry);
    }

    @Bean
    public Counter paymentFailureCounter(MeterRegistry registry) {
        return Counter.builder("payments.failure.total")
            .description("Total number of failed payments")
            .register(registry);
    }
}
