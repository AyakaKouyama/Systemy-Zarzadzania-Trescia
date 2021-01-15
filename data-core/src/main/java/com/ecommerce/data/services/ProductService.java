package com.ecommerce.data.services;

import com.ecommerce.data.entities.Product;

import java.util.List;

public interface ProductService {

    void save(Product product);

    List<Product> getAllProducts();

    List<Product> getAllActiveProducts();

    List<Product> getAllActiveProductsByCategoryId(Long categoryId);

    Product getProductById(Long id);

    Product getPublicProductById(Long id);

    void deleteProduct(Long productId);

    void activateProduct(Long productId);

}
