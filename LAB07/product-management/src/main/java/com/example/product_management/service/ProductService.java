package com.example.product_management.service;

import com.example.product_management.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<String> getAllCategories();

    List<Product> getAllProducts(Sort sort);

    Page<Product> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable);

    long countProductsByCategory(String category);

    BigDecimal getTotalValue();

    BigDecimal getAveragePrice();

    List<Product> getLowStockProducts(int threshold);

    List<Product> getRecentProducts();
}
