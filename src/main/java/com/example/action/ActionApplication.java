package com.example.action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 4 + Java 25 Example Application
 *
 * This application demonstrates the latest features of:
 * - Java 25 (LTS): Virtual Threads, Structured Concurrency, Pattern Matching for Primitives, Scoped Values
 * - Spring Boot 4.0: HTTP Interface Client, API Versioning, Enhanced Configuration
 */
@SpringBootApplication
public class ActionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActionApplication.class, args);
    }
}
