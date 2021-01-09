package com.ecommerce.data.services;

import com.ecommerce.data.entities.Category;
import com.ecommerce.data.exceptions.AdminException;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    void save(Category category);

    Category getCategoryById(Long id);

    void deleteCategory(String productId) throws AdminException;

    void activateCategory(String productId, Boolean active);
}
