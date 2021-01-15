package com.ecommerce.data.services;

import com.ecommerce.data.entities.Category;
import com.ecommerce.data.exceptions.AdminException;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    List<Category> getAllActiveCategories();

    void save(Category category);

    Category getCategoryById(Long id);

    void deleteCategory(Long categoryId) throws AdminException;

    void activateCategory(Long categoryId);
}
