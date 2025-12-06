package com.example.springboot4study.controller;

import com.example.springboot4study.domain.Product;
import com.example.springboot4study.java25.PatternMatchingExample;
import com.example.springboot4study.java25.StructuredConcurrencyExample;
import com.example.springboot4study.java25.VirtualThreadExample;
import com.example.springboot4study.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Demo Controller showcasing Spring Boot 4 + Java 25 features.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final PatternMatchingExample patternMatchingExample;
    private final StructuredConcurrencyExample structuredConcurrencyExample;
    private final VirtualThreadExample virtualThreadExample;
    private final ProductRepository productRepository;

    public DemoController(
            PatternMatchingExample patternMatchingExample,
            StructuredConcurrencyExample structuredConcurrencyExample,
            VirtualThreadExample virtualThreadExample,
            ProductRepository productRepository
    ) {
        this.patternMatchingExample = patternMatchingExample;
        this.structuredConcurrencyExample = structuredConcurrencyExample;
        this.virtualThreadExample = virtualThreadExample;
        this.productRepository = productRepository;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "java.version", System.getProperty("java.version"),
                "spring.boot.version", "4.0.0",
                "virtual.threads.enabled", true
        );
    }

    /**
     * Demonstrates Java 25 Pattern Matching with primitives
     */
    @GetMapping("/pattern-matching/{value}")
    public Map<String, String> testPatternMatching(@PathVariable String value) {
        // Try to parse as different types
        Object parsed;
        try {
            if (value.contains(".")) {
                parsed = Double.parseDouble(value);
            } else {
                parsed = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            parsed = value;
        }

        String result = patternMatchingExample.classifyNumber(parsed);
        return Map.of("input", value, "classification", result);
    }

    /**
     * Demonstrates Java 25 Pattern Matching for HTTP status codes
     */
    @GetMapping("/http-status/{code}")
    public Map<String, Object> classifyHttpStatus(@PathVariable int code) {
        String classification = patternMatchingExample.classifyHttpStatus(code);
        return Map.of(
                "statusCode", code,
                "classification", classification
        );
    }

    /**
     * Demonstrates Java 25 Structured Concurrency
     */
    @GetMapping("/structured-concurrency/{userId}")
    public ResponseEntity<?> fetchUserDashboard(@PathVariable String userId) {
        try {
            var dashboard = structuredConcurrencyExample.fetchUserDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Demonstrates Java 25 Virtual Threads - massive concurrency
     */
    @GetMapping("/virtual-threads/benchmark")
    public ResponseEntity<?> benchmarkVirtualThreads(
            @RequestParam(defaultValue = "1000") int taskCount
    ) {
        try {
            var result = virtualThreadExample.benchmarkVirtualThreads(taskCount);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Demonstrates Virtual Threads with concurrent HTTP-like requests
     */
    @GetMapping("/virtual-threads/requests")
    public ResponseEntity<?> processMultipleRequests() {
        try {
            List<String> urls = List.of(
                    "https://api.example.com/users",
                    "https://api.example.com/products",
                    "https://api.example.com/orders",
                    "https://api.example.com/reviews",
                    "https://api.example.com/inventory"
            );
            var results = virtualThreadExample.processMultipleRequests(urls);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Product CRUD endpoints
    @GetMapping("/products")
    public List<Product.ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(Product.ProductDTO::from)
                .toList();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product.ProductDTO> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(Product.ProductDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public Product.ProductDTO createProduct(@RequestBody Product.CreateProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.category()
        );
        return Product.ProductDTO.from(productRepository.save(product));
    }

    @GetMapping("/products/category/{category}")
    public List<Product.ProductDTO> getProductsByCategory(@PathVariable String category) {
        return productRepository.findByCategory(category).stream()
                .map(Product.ProductDTO::from)
                .toList();
    }

    @GetMapping("/products/search")
    public List<Product.ProductDTO> searchProducts(@RequestParam String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(Product.ProductDTO::from)
                .toList();
    }

    @GetMapping("/products/price-range")
    public List<Product.ProductDTO> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max
    ) {
        return productRepository.findByPriceBetween(min, max).stream()
                .map(Product.ProductDTO::from)
                .toList();
    }
}
