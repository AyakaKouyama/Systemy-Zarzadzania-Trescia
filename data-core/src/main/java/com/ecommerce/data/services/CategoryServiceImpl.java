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
    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCategory(String productId) throws AdminException {
        Category category = getCategoryById(Long.parseLong(productId));
        if(category != null && CollectionUtils.isEmpty(category.getProducts())){
            categoryRepository.delete(category);
        }else if(category != null && !CollectionUtils.isEmpty(category.getProducts())){
            throw new AdminException("Nie można usunąć kategorii, która posiada przypisane produkty.");
        }
    }

    @Override
    public void activateCategory(String productId, Boolean active) {
        Category category = getCategoryById(Long.parseLong(productId));
        if(category != null){
            category.setActive(!active);
            categoryRepository.save(category);
        }
    }
}
