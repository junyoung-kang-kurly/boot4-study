package com.example.action.repository;

import com.example.action.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Repository demonstrating Spring Data JPA features.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Method name based queries
    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByStockQuantityLessThan(Integer threshold);

    // JPQL query
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.createdAt DESC")
    List<Product> findAllInStock();

    // Native query
    @Query(value = "SELECT * FROM products WHERE category = ?1 AND stock_quantity > 0", nativeQuery = true)
    List<Product> findAvailableByCategory(String category);
}
