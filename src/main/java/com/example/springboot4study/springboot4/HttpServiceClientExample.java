package com.example.springboot4study.springboot4;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

/**
 * Spring Boot 4 Feature: HTTP Interface Client (Declarative HTTP Client)
 *
 * HTTP Interface Client allows you to define HTTP services using annotated Java interfaces.
 * Spring automatically generates the implementation at runtime.
 *
 * Benefits:
 * - Declarative: Define APIs with annotations, no boilerplate code
 * - Type-safe: Compile-time checking of request/response types
 * - Testable: Easy to mock interfaces for testing
 * - Flexible: Supports RestClient, WebClient, or RestTemplate backends
 */
@Service
public class HttpServiceClientExample {

    // DTOs for the API
    public record User(Long id, String name, String email) {}
    public record CreateUserRequest(String name, String email) {}
    public record Post(Long id, Long userId, String title, String body) {}
    public record Todo(Long id, Long userId, String title, boolean completed) {}

    /**
     * Example 1: Basic HTTP Interface with CRUD operations
     * The @HttpExchange annotation defines the base URL for all methods.
     */
    @HttpExchange("/users")
    public interface UserServiceClient {

        @GetExchange
        List<User> getAllUsers();

        @GetExchange("/{id}")
        User getUserById(@PathVariable Long id);

        @PostExchange
        User createUser(@RequestBody CreateUserRequest request);

        @PutExchange("/{id}")
        User updateUser(@PathVariable Long id, @RequestBody User user);

        @DeleteExchange("/{id}")
        void deleteUser(@PathVariable Long id);
    }

    /**
     * Example 2: Posts API client with query parameters
     */
    @HttpExchange("/posts")
    public interface PostServiceClient {

        @GetExchange
        List<Post> getAllPosts();

        @GetExchange("/{id}")
        Post getPostById(@PathVariable Long id);

        @GetExchange
        List<Post> getPostsByUserId(@PathVariable("userId") Long userId);

        @PostExchange
        Post createPost(@RequestBody Post post);
    }

    /**
     * Example 3: Todo API client
     */
    @HttpExchange("/todos")
    public interface TodoServiceClient {

        @GetExchange
        List<Todo> getAllTodos();

        @GetExchange("/{id}")
        Todo getTodoById(@PathVariable Long id);

        @PostExchange
        Todo createTodo(@RequestBody Todo todo);

        @PutExchange("/{id}")
        Todo updateTodo(@PathVariable Long id, @RequestBody Todo todo);
    }

    /**
     * Example 4: GitHub API client (real-world example)
     */
    public record GitHubRepo(Long id, String name, String full_name, String description, int stargazers_count) {}
    public record GitHubUser(Long id, String login, String name, String company) {}

    @HttpExchange
    public interface GitHubClient {

        @GetExchange("/users/{username}")
        GitHubUser getUser(@PathVariable String username);

        @GetExchange("/users/{username}/repos")
        List<GitHubRepo> getUserRepos(@PathVariable String username);

        @GetExchange("/repos/{owner}/{repo}")
        GitHubRepo getRepository(@PathVariable String owner, @PathVariable String repo);
    }
}
