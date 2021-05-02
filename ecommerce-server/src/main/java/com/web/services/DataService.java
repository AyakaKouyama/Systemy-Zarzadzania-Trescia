package com.web.services;


import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Order;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.services.CategoryService;
import com.ecommerce.data.services.DbOrderService;
import com.ecommerce.data.services.ImageService;
import com.ecommerce.data.services.ProductService;
import com.ecommerce.data.services.UserService;
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
    private final DbOrderService orderService;

    @Autowired
    public DataService(CategoryService categoryService,
            ProductService productService,
            UserService userService,
            ImageService imageService,
            DbOrderService orderService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.imageService = imageService;
        this.orderService = orderService;
    }

    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public List<Product> getAllPublicProducts() {
        return productService.getAllActiveProducts();
    }

    public List<Category> getAllPublicCategories() {
        return categoryService.getAllActiveCategories();
    }

    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }

    public List<Product> getAllPublicProductsbyCategoryId(Long categoryId) {
        return productService.getAllActiveProductsByCategoryId(categoryId);
    }

    public Product getPublicProductbyId(Long categoryId) {
        return productService.getPublicProductById(categoryId);
    }

    public void createCategory(String name, Long id) throws AdminException {
        Category category = new Category();
        category.setName(name);
        category.setId(id);
        categoryService.save(category);
    }

    public Product createProduct(String name,
            String sku,
            String shortDesc,
            String desc,
            BigDecimal price,
            BigInteger qty,
            List<String> categories,
            String userLogin,
            Long id) {
        Product dest = new Product();
        dest.setName(name);
        dest.setSku(sku);
        dest.setShortDescription(shortDesc);
        dest.setDescription(desc);
        dest.setPrice(price);
        dest.setQuantity(qty);
        if (!CollectionUtils.isEmpty(categories)) {
            List<Category> cat = new ArrayList<>();
            categories.forEach(f -> cat.add(getCategoryById(Long.parseLong(f))));
            dest.getCategories().addAll(cat);
        }

        dest.setCreator(userService.findUserByLogin(userLogin));
        dest.setCreateDate(new Date());
        dest.setActive(true);
        dest.setId(id);

        productService.save(dest);

        return dest;
    }

    public Product getProductById(Long productId) {
        return productService.getProductById(productId);
    }

    public Category getCategoryById(Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    public void deleteProduct(Long productId) throws AdminException {
        productService.deleteProduct(productId);
    }

    public void activateProduct(Long productId) {
        productService.activateProduct(productId);
    }

    public void deleteCategory(Long categoryId) throws AdminException {
        categoryService.deleteCategory(categoryId);
    }

    public void activateCategory(Long categoryId) {
        categoryService.activateCategory(categoryId);
    }

    public void deleteImage(Long id) throws AdminException {
        imageService.delete(id);
    }
}
