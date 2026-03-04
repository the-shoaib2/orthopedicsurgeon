package com.orthopedic.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    // Removed CommonsRequestLoggingFilter to use the new custom
    // RequestLoggingFilter
    // which provides a cleaner Next.js-style output with latency.
}
