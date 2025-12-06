package com.example.springboot4study.springboot4;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Boot 4 Feature: API Versioning Support
 *
 * Spring Boot 4 introduces built-in support for API versioning through:
 * - URL path versioning: /api/v1/users, /api/v2/users
 * - Header versioning: Accept-Version: v1
 * - Query parameter versioning: /api/users?version=1
 *
 * Configuration is done via spring.mvc.apiversion.* properties.
 */
public class ApiVersioningExample {

    // DTO for V1 API - simpler structure
    public record UserV1(Long id, String name, String email) {}

    // DTO for V2 API - enhanced with additional fields
    public record UserV2(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressV2 address,
            LocalDateTime createdAt
    ) {}

    public record AddressV2(String street, String city, String country, String zipCode) {}

    // DTO for V3 API - full profile with preferences
    public record UserV3(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressV2 address,
            UserPreferencesV3 preferences,
            List<String> roles,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {}

    public record UserPreferencesV3(String theme, String language, boolean notifications) {}

    /**
     * V1 API Controller - Basic user information
     */
    @RestController
    @RequestMapping("/api/v1/users")
    public static class UserControllerV1 {

        @GetMapping
        public List<UserV1> getAllUsers() {
            return List.of(
                    new UserV1(1L, "John Doe", "john@example.com"),
                    new UserV1(2L, "Jane Smith", "jane@example.com")
            );
        }

        @GetMapping("/{id}")
        public UserV1 getUserById(@PathVariable Long id) {
            return new UserV1(id, "John Doe", "john@example.com");
        }
    }

    /**
     * V2 API Controller - Enhanced user information with address
     */
    @RestController
    @RequestMapping("/api/v2/users")
    public static class UserControllerV2 {

        @GetMapping
        public List<UserV2> getAllUsers() {
            return List.of(
                    new UserV2(
                            1L, "John", "Doe", "john@example.com", "+1-555-0101",
                            new AddressV2("123 Main St", "New York", "USA", "10001"),
                            LocalDateTime.now().minusDays(30)
                    ),
                    new UserV2(
                            2L, "Jane", "Smith", "jane@example.com", "+1-555-0102",
                            new AddressV2("456 Oak Ave", "Los Angeles", "USA", "90001"),
                            LocalDateTime.now().minusDays(15)
                    )
            );
        }

        @GetMapping("/{id}")
        public UserV2 getUserById(@PathVariable Long id) {
            return new UserV2(
                    id, "John", "Doe", "john@example.com", "+1-555-0101",
                    new AddressV2("123 Main St", "New York", "USA", "10001"),
                    LocalDateTime.now().minusDays(30)
            );
        }
    }

    /**
     * V3 API Controller - Full user profile with preferences and roles
     */
    @RestController
    @RequestMapping("/api/v3/users")
    public static class UserControllerV3 {

        @GetMapping
        public List<UserV3> getAllUsers() {
            return List.of(
                    new UserV3(
                            1L, "John", "Doe", "john@example.com", "+1-555-0101",
                            new AddressV2("123 Main St", "New York", "USA", "10001"),
                            new UserPreferencesV3("dark", "en", true),
                            List.of("USER", "ADMIN"),
                            LocalDateTime.now().minusDays(30),
                            LocalDateTime.now().minusHours(2)
                    ),
                    new UserV3(
                            2L, "Jane", "Smith", "jane@example.com", "+1-555-0102",
                            new AddressV2("456 Oak Ave", "Los Angeles", "USA", "90001"),
                            new UserPreferencesV3("light", "en", false),
                            List.of("USER"),
                            LocalDateTime.now().minusDays(15),
                            LocalDateTime.now().minusMinutes(30)
                    )
            );
        }

        @GetMapping("/{id}")
        public UserV3 getUserById(@PathVariable Long id) {
            return new UserV3(
                    id, "John", "Doe", "john@example.com", "+1-555-0101",
                    new AddressV2("123 Main St", "New York", "USA", "10001"),
                    new UserPreferencesV3("dark", "en", true),
                    List.of("USER", "ADMIN"),
                    LocalDateTime.now().minusDays(30),
                    LocalDateTime.now().minusHours(2)
            );
        }
    }
}
