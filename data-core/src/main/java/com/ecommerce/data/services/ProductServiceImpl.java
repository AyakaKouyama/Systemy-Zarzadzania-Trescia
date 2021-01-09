package com.ecommerce.data.services;

import com.ecommerce.data.entities.Product;
import com.ecommerce.data.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = getProductById(Long.parseLong(productId));
        if(product != null){
            productRepository.delete(product);
        }
    }

    @Override
    public void activateProduct(String productId, Boolean active) {
        Product product = getProductById(Long.parseLong(productId));
        if(product != null){
            product.setActive(!active);
            productRepository.save(product);
        }
    }
}
