package com.example.action.java25;

import org.springframework.stereotype.Component;

import java.util.concurrent.StructuredTaskScope;

/**
 * Java 25 Feature: Scoped Values (Finalized in Java 25)
 *
 * Scoped Values are a modern alternative to ThreadLocal for sharing immutable data
 * within a thread and its child threads (especially virtual threads).
 *
 * Key benefits over ThreadLocal:
 * - Immutable: Values cannot be changed once bound
 * - Automatic cleanup: No memory leaks from forgotten removes
 * - Inheritance-friendly: Automatically inherited by child threads
 * - Designed for virtual threads: Efficient with millions of threads
 */
@Component
public class ScopedValueExample {

    // Define scoped values at class level
    private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<SecurityContext> SECURITY_CONTEXT = ScopedValue.newInstance();

    public record SecurityContext(String userId, String role, String tenantId) {}

    /**
     * Basic usage: Binding a scoped value for a block of code.
     */
    public String processWithUser(String userId, Runnable task) {
        // The value is only accessible within this scope
        ScopedValue.runWhere(CURRENT_USER, userId, task);
        return "Completed for user: " + userId;
    }

    /**
     * Demonstrates accessing scoped values from any depth in the call stack.
     */
    public void handleRequest(String requestId, String userId) {
        ScopedValue.runWhere(REQUEST_ID, requestId, () -> {
            ScopedValue.runWhere(CURRENT_USER, userId, () -> {
                processRequest();
            });
        });
    }

    private void processRequest() {
        // Access scoped values anywhere in the call stack
        String requestId = REQUEST_ID.get();
        String userId = CURRENT_USER.get();

        System.out.println("Processing request " + requestId + " for user " + userId);
        validateRequest();
        executeBusinessLogic();
    }

    private void validateRequest() {
        // Scoped values are accessible here without passing parameters
        System.out.println("Validating request: " + REQUEST_ID.get());
    }

    private void executeBusinessLogic() {
        System.out.println("Executing logic for user: " + CURRENT_USER.get());
    }

    /**
     * Using scoped values with callWhere to return a value.
     */
    public String computeWithContext(String userId, String role) {
        SecurityContext context = new SecurityContext(userId, role, "default-tenant");

        return ScopedValue.callWhere(SECURITY_CONTEXT, context, () -> {
            return performSecureOperation();
        });
    }

    private String performSecureOperation() {
        SecurityContext ctx = SECURITY_CONTEXT.get();
        return "Operation completed for " + ctx.userId() + " with role " + ctx.role();
    }

    /**
     * Demonstrates scoped value inheritance with structured concurrency.
     * Child tasks automatically inherit scoped values from the parent.
     */
    public record UserData(String profile, String preferences) {}

    public UserData fetchUserDataWithContext(String userId) throws Exception {
        return ScopedValue.callWhere(CURRENT_USER, userId, () -> {
            try (var scope = StructuredTaskScope.open(
                    StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {

                // Child tasks inherit CURRENT_USER scoped value
                var profileTask = scope.fork(this::fetchProfile);
                var prefsTask = scope.fork(this::fetchPreferences);

                scope.join();

                return new UserData(profileTask.get(), prefsTask.get());
            }
        });
    }

    private String fetchProfile() throws InterruptedException {
        Thread.sleep(50);
        // Access inherited scoped value
        return "Profile for: " + CURRENT_USER.get();
    }

    private String fetchPreferences() throws InterruptedException {
        Thread.sleep(30);
        // Access inherited scoped value
        return "Preferences for: " + CURRENT_USER.get();
    }

    /**
     * Demonstrates checking if a scoped value is bound.
     */
    public String safeGetCurrentUser() {
        if (CURRENT_USER.isBound()) {
            return CURRENT_USER.get();
        }
        return "anonymous";
    }

    /**
     * Demonstrates rebinding (shadowing) a scoped value.
     */
    public void demonstrateShadowing() {
        ScopedValue.runWhere(CURRENT_USER, "outer-user", () -> {
            System.out.println("Outer scope user: " + CURRENT_USER.get()); // "outer-user"

            // Shadow the outer binding with a new value
            ScopedValue.runWhere(CURRENT_USER, "inner-user", () -> {
                System.out.println("Inner scope user: " + CURRENT_USER.get()); // "inner-user"
            });

            // Back to original binding after inner scope ends
            System.out.println("Back to outer: " + CURRENT_USER.get()); // "outer-user"
        });
    }

    /**
     * Real-world example: Multi-tenant request handling.
     */
    public void handleMultiTenantRequest(String tenantId, String userId, String requestId) {
        SecurityContext context = new SecurityContext(userId, "USER", tenantId);

        ScopedValue.runWhere(SECURITY_CONTEXT, context, () -> {
            ScopedValue.runWhere(REQUEST_ID, requestId, () -> {
                processMultiTenantRequest();
            });
        });
    }

    private void processMultiTenantRequest() {
        SecurityContext ctx = SECURITY_CONTEXT.get();
        String reqId = REQUEST_ID.get();

        System.out.println("Tenant: " + ctx.tenantId());
        System.out.println("User: " + ctx.userId());
        System.out.println("Request: " + reqId);

        // All downstream calls have access to this context
        auditLog();
        performTenantSpecificOperation();
    }

    private void auditLog() {
        SecurityContext ctx = SECURITY_CONTEXT.get();
        System.out.println("Audit: " + ctx.userId() + " @ " + ctx.tenantId());
    }

    private void performTenantSpecificOperation() {
        SecurityContext ctx = SECURITY_CONTEXT.get();
        System.out.println("Operation for tenant: " + ctx.tenantId());
    }
}
