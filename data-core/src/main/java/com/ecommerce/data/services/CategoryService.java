package com.ecommerce.data.services;

import com.ecommerce.data.entities.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    void save(Category category);

    Category getCategoryById(Long id);
}
