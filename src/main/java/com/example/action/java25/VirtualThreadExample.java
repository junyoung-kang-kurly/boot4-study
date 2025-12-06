package com.example.action.java25;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Java 25 Feature: Virtual Threads (Finalized in Java 21, Enhanced in Java 25)
 *
 * Virtual threads are lightweight threads that dramatically reduce the effort
 * of writing, maintaining, and observing high-throughput concurrent applications.
 *
 * Key characteristics:
 * - Lightweight: Can create millions of virtual threads
 * - Cheap to create: No OS thread allocation overhead
 * - Efficient blocking: Blocking operations don't block OS threads
 * - Compatible: Works with existing Thread API
 */
@Component
public class VirtualThreadExample {

    /**
     * Demonstrates creating and running virtual threads directly.
     */
    public void basicVirtualThread() throws InterruptedException {
        // Create a virtual thread using the builder
        Thread virtualThread = Thread.ofVirtual()
                .name("my-virtual-thread")
                .start(() -> {
                    System.out.println("Running in virtual thread: " + Thread.currentThread());
                    System.out.println("Is virtual: " + Thread.currentThread().isVirtual());
                });

        virtualThread.join();
    }

    /**
     * Demonstrates the power of virtual threads - handling massive concurrency.
     * This would be impractical with platform threads.
     */
    public record TaskResult(int taskId, long durationMs, String threadName) {}

    public List<TaskResult> runMassiveConcurrentTasks(int taskCount) throws InterruptedException {
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<TaskResult>> futures = IntStream.range(0, taskCount)
                    .mapToObj(id -> executor.submit(() -> {
                        Instant taskStart = Instant.now();
                        // Simulate I/O operation (e.g., database query, HTTP call)
                        Thread.sleep(Duration.ofMillis(100));
                        long duration = Duration.between(taskStart, Instant.now()).toMillis();
                        return new TaskResult(id, duration, Thread.currentThread().toString());
                    }))
                    .toList();

            return futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }
    }

    /**
     * Demonstrates virtual thread factory with custom naming.
     */
    public ExecutorService createNamedVirtualThreadExecutor(String prefix) {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                        .name(prefix + "-", 0)
                        .factory()
        );
    }

    /**
     * Demonstrates comparison between platform threads and virtual threads.
     */
    public record ThreadComparison(
            String threadType,
            int taskCount,
            long totalDurationMs,
            double tasksPerSecond
    ) {}

    public ThreadComparison benchmarkVirtualThreads(int taskCount) throws Exception {
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = IntStream.range(0, taskCount)
                    .mapToObj(id -> executor.submit(() -> {
                        try {
                            Thread.sleep(Duration.ofMillis(10));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }))
                    .toList();

            for (Future<?> future : futures) {
                future.get();
            }
        }

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        double tasksPerSecond = (taskCount * 1000.0) / durationMs;

        return new ThreadComparison("Virtual", taskCount, durationMs, tasksPerSecond);
    }

    /**
     * Demonstrates thread-local behavior with virtual threads.
     * Important: Avoid thread-locals with virtual threads when possible,
     * use Scoped Values instead (see ScopedValueExample).
     */
    public void demonstrateThreadLocalCaution() {
        // Thread-locals still work but can cause memory issues with many virtual threads
        ThreadLocal<String> context = new ThreadLocal<>();

        Thread.ofVirtual().start(() -> {
            context.set("Virtual Thread Context");
            System.out.println("Context: " + context.get());
            // Important: Always clean up thread-locals in virtual threads
            context.remove();
        });
    }

    /**
     * Real-world example: Processing multiple HTTP-like requests concurrently.
     */
    public record HttpResponse(int statusCode, String body, long latencyMs) {}

    public List<HttpResponse> processMultipleRequests(List<String> urls) throws Exception {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<HttpResponse>> futures = urls.stream()
                    .map(url -> executor.submit(() -> simulateHttpRequest(url)))
                    .toList();

            return futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            return new HttpResponse(500, "Error: " + e.getMessage(), 0);
                        }
                    })
                    .toList();
        }
    }

    private HttpResponse simulateHttpRequest(String url) throws InterruptedException {
        Instant start = Instant.now();
        // Simulate network latency
        Thread.sleep(Duration.ofMillis((long) (Math.random() * 200 + 50)));
        long latency = Duration.between(start, Instant.now()).toMillis();
        return new HttpResponse(200, "Response from " + url, latency);
    }
}
