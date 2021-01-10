package com.ecommerce.data.services;

import com.ecommerce.data.entities.Product;

import java.util.List;

public interface ProductService {

    void save(Product product);

    List<Product> getAllProducts();

    List<Product> getAllActiveProducts();

    List<Product> getAllActiveProductsByCategoryId(String categoryId);

    Product getProductById(Long id);

    void deleteProduct(String productId);

    void activateProduct(String productId, Boolean active);

}
