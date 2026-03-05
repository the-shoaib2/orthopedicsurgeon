package com.orthopedic.api.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * DataInitializer is now inactive to ensure a clean database state.
 * Seeding and wipe logic have been removed.
 */
@Configuration
@Profile("local")
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Database is clean. No automated actions enabled.
    }
}
