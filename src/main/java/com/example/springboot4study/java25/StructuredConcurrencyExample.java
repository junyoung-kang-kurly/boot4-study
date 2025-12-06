package com.example.springboot4study.java25;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ExecutionException;

/**
 * Java 25 Feature: Structured Concurrency (JEP 505 - Fifth Preview)
 *
 * Structured concurrency treats groups of related tasks running in different threads
 * as a single unit of work. This simplifies error handling, cancellation, and observability.
 *
 * Key benefits:
 * - Automatic cancellation of sibling tasks when one fails
 * - Clear parent-child relationship between tasks
 * - Improved debugging and monitoring
 * - Works seamlessly with Virtual Threads
 */
@Component
public class StructuredConcurrencyExample {

    public record UserProfile(String userId, String name, String email) {}
    public record UserOrders(String userId, List<String> orderIds) {}
    public record UserRecommendations(String userId, List<String> productIds) {}
    public record UserDashboard(UserProfile profile, UserOrders orders, UserRecommendations recommendations) {}

    /**
     * Demonstrates ShutdownOnFailure strategy.
     * If any subtask fails, all other subtasks are cancelled immediately.
     * This is useful when all results are required for success.
     */
    public UserDashboard fetchUserDashboard(String userId) throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {

            // Fork multiple concurrent tasks
            var profileTask = scope.fork(() -> fetchUserProfile(userId));
            var ordersTask = scope.fork(() -> fetchUserOrders(userId));
            var recommendationsTask = scope.fork(() -> fetchRecommendations(userId));

            // Wait for all tasks to complete (or fail fast if any fails)
            scope.join();

            // All tasks succeeded, combine results
            return new UserDashboard(
                    profileTask.get(),
                    ordersTask.get(),
                    recommendationsTask.get()
            );
        }
    }

    /**
     * Demonstrates ShutdownOnSuccess strategy.
     * Returns as soon as any subtask succeeds, cancelling others.
     * Useful for racing multiple alternatives (e.g., querying multiple services).
     */
    public String fetchFromFastestSource(String key) throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.anySuccessfulResultOrThrow())) {

            // Race multiple data sources
            scope.fork(() -> fetchFromPrimaryCache(key));
            scope.fork(() -> fetchFromSecondaryCache(key));
            scope.fork(() -> fetchFromDatabase(key));

            // Returns first successful result, cancels others
            return scope.join();
        }
    }

    /**
     * Demonstrates collecting all results (both successes and failures).
     */
    public record BatchResult<T>(List<T> successes, List<Throwable> failures) {}

    public BatchResult<String> processBatch(List<String> items) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {

            List<StructuredTaskScope.Subtask<String>> subtasks = items.stream()
                    .map(item -> scope.fork(() -> processItem(item)))
                    .toList();

            scope.join();

            List<String> results = subtasks.stream()
                    .map(StructuredTaskScope.Subtask::get)
                    .toList();

            return new BatchResult<>(results, List.of());
        } catch (ExecutionException e) {
            return new BatchResult<>(List.of(), List.of(e.getCause()));
        }
    }

    /**
     * Demonstrates timeout handling with structured concurrency.
     */
    public UserDashboard fetchUserDashboardWithTimeout(String userId, Duration timeout)
            throws InterruptedException, ExecutionException {
        Instant deadline = Instant.now().plus(timeout);

        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow(),
                config -> config.withTimeout(timeout))) {

            var profileTask = scope.fork(() -> fetchUserProfile(userId));
            var ordersTask = scope.fork(() -> fetchUserOrders(userId));
            var recommendationsTask = scope.fork(() -> fetchRecommendations(userId));

            scope.join();

            return new UserDashboard(
                    profileTask.get(),
                    ordersTask.get(),
                    recommendationsTask.get()
            );
        }
    }

    // Simulated service methods
    private UserProfile fetchUserProfile(String userId) throws InterruptedException {
        Thread.sleep(100); // Simulate network call
        return new UserProfile(userId, "John Doe", "john@example.com");
    }

    private UserOrders fetchUserOrders(String userId) throws InterruptedException {
        Thread.sleep(150); // Simulate network call
        return new UserOrders(userId, List.of("ORD-001", "ORD-002", "ORD-003"));
    }

    private UserRecommendations fetchRecommendations(String userId) throws InterruptedException {
        Thread.sleep(120); // Simulate network call
        return new UserRecommendations(userId, List.of("PROD-A", "PROD-B", "PROD-C"));
    }

    private String fetchFromPrimaryCache(String key) throws InterruptedException {
        Thread.sleep(50);
        return "Primary: " + key;
    }

    private String fetchFromSecondaryCache(String key) throws InterruptedException {
        Thread.sleep(100);
        return "Secondary: " + key;
    }

    private String fetchFromDatabase(String key) throws InterruptedException {
        Thread.sleep(200);
        return "Database: " + key;
    }

    private String processItem(String item) throws InterruptedException {
        Thread.sleep(50);
        return "Processed: " + item;
    }
}
