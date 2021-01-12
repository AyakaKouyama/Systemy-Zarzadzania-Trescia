package com.web.services;


import com.ecommerce.data.dtos.ProductDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.services.CategoryService;
import com.ecommerce.data.services.ImageService;
import com.ecommerce.data.services.ProductService;
import com.ecommerce.data.services.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public DataService(CategoryService categoryService, ProductService productService, UserService userService, ImageService imageService){
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.imageService = imageService;
    }

    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }

    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    public List<Product> getAllPublicProducts(){
        return productService.getAllActiveProducts();
    }

    public List<Product> getAllPublicProductsbyCategoryId(String categoryId){
        return productService.getAllActiveProductsByCategoryId(categoryId);
    }

    public Product getPublicProductbyId(String categoryId){
        return productService.getPublicProductById(Long.parseLong(categoryId));
    }

    public void createCategory(String name){
        Category category = new Category();
        category.setName(name);
        categoryService.save(category);
    }

    public Product createProduct(String name, String sku, String shortDesc, String desc, BigDecimal price, BigInteger qty, List<String> categories, String userLogin){
        Product dest = new Product();
        dest.setName(name);
        dest.setSku(sku);
        dest.setShortDescription(shortDesc);
        dest.setDescription(desc);
        dest.setPrice(price);
        dest.setQuantity(qty);
        if(!CollectionUtils.isEmpty(categories)) {
            List<Category> cat = new ArrayList<>();
            categories.forEach(f -> cat.add(getCategoryById(Long.parseLong(f))));
            dest.getCategories().addAll(cat);
        }

        dest.setCreator(userService.findUserByLogin(userLogin));
        dest.setCreateDate(new Date());
        dest.setActive(true);

        productService.save(dest);

        return dest;
    }

    public Product getProductById(String productId){
        return productService.getProductById(Long.parseLong(productId));
    }

    public Category getCategoryById(Long categoryId){
        return categoryService.getCategoryById(categoryId);
    }

    public void deleteProduct(String productId){
        productService.deleteProduct(productId);
    }

    public void activateProduct(String productId, Boolean active){
        productService.activateProduct(productId, active);
    }

    public void deleteCategory(String categoryId) throws AdminException {
       categoryService.deleteCategory(categoryId);
    }

    public void activateCategory(String categoryId, Boolean active){
        categoryService.activateCategory(categoryId, active);
    }

    public void deleteImage(String id) throws AdminException{
        imageService.delete(id);
    }
}
