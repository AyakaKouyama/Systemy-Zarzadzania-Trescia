package com.ecommerce.data.services;

import com.ecommerce.data.entities.Product;

import java.util.List;

public interface ProductService {

    void save(Product product);

    List<Product> getAllProducts();

    Product getProductById(Long id);
}
