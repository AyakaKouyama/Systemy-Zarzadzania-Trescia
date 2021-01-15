package com.ecommerce.data.services;

import com.ecommerce.data.entities.Category;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllActive();
    }

    @Override
    public void save(Category category) throws AdminException {
        Category cat = categoryRepository.findByName(category.getName());
        if (cat != null && category.getId() == null) {
            throw new AdminException("Category with this name already exists.");
        }
        categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCategory(Long categoryId) throws AdminException {
        Category category = getCategoryById(categoryId);
        if (category != null && CollectionUtils.isEmpty(category.getProducts())) {
            categoryRepository.delete(category);
        } else if (category != null && !CollectionUtils.isEmpty(category.getProducts())) {
            throw new AdminException("Cannot delete category that has assigned products.");
        }else if(category == null){
            throw new AdminException("Cannot delete category. Category with id " + categoryId + " doesn't exist.");
        }
    }

    @Override
    public void activateCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        if (category != null) {
            category.setActive(!category.getActive());
            categoryRepository.save(category);
        }else{
            throw new AdminException("Cannot activate/deactivate category. Category with id " + categoryId + " doesn't exists.");
        }
    }
}
