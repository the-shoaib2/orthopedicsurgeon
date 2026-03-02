package com.orthopedic.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@org.springframework.scheduling.annotation.EnableScheduling
@org.springframework.scheduling.annotation.EnableAsync
public class OrthopedicApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrthopedicApplication.class, args);
    }
}
