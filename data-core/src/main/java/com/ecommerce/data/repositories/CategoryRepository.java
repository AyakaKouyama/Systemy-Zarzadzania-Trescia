package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAll();

    Category findByName(@Param("name") String name);

    @Query("SELECT c FROM Category c WHERE  c.active = true")
    List<Category> findAllActive();
}
