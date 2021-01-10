package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> getAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true and :category MEMBER OF p.categories")
    List<Product> getAllActiveProductsByCategory(@Param("category")Category category);
}
