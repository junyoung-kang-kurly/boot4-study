package com.example.action.springboot4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Spring Boot 4 Feature: HTTP Interface Client Configuration
 *
 * This configuration class shows how to create HTTP Interface Client instances.
 * Spring Boot 4 provides auto-configuration, but you can also configure manually
 * for more control over timeout, headers, and error handling.
 */
@Configuration
public class HttpClientConfig {

    /**
     * Creates a RestClient configured for JSONPlaceholder API.
     * RestClient is the modern synchronous HTTP client in Spring 6.1+
     */
    @Bean
    public RestClient jsonPlaceholderRestClient() {
        return RestClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Creates the UserServiceClient using HTTP Interface.
     * The proxy is automatically generated from the interface.
     */
    @Bean
    public HttpServiceClientExample.UserServiceClient userServiceClient(RestClient jsonPlaceholderRestClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(jsonPlaceholderRestClient))
                .build()
                .createClient(HttpServiceClientExample.UserServiceClient.class);
    }

    /**
     * Creates the PostServiceClient.
     */
    @Bean
    public HttpServiceClientExample.PostServiceClient postServiceClient(RestClient jsonPlaceholderRestClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(jsonPlaceholderRestClient))
                .build()
                .createClient(HttpServiceClientExample.PostServiceClient.class);
    }

    /**
     * Creates the TodoServiceClient.
     */
    @Bean
    public HttpServiceClientExample.TodoServiceClient todoServiceClient(RestClient jsonPlaceholderRestClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(jsonPlaceholderRestClient))
                .build()
                .createClient(HttpServiceClientExample.TodoServiceClient.class);
    }

    /**
     * Creates a RestClient for GitHub API with custom configuration.
     */
    @Bean
    public RestClient gitHubRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .defaultHeader("User-Agent", "Spring-Boot-4-Example")
                .build();
    }

    /**
     * Creates the GitHubClient using HTTP Interface.
     */
    @Bean
    public HttpServiceClientExample.GitHubClient gitHubClient(RestClient gitHubRestClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(gitHubRestClient))
                .build()
                .createClient(HttpServiceClientExample.GitHubClient.class);
    }
}
