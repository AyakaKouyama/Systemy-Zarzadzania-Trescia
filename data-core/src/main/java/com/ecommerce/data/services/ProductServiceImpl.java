package com.ecommerce.data.services;

import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Override
    public void save(Product product) {
        Product p = productRepository.findByName(product.getName());
        if (p != null && product.getId() == null) {
            throw new AdminException("Product with this name already exists.");
        }

        productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllActiveProducts() {
        List<Product> products = productRepository.getAllActiveProducts();
        products.forEach(p -> p.setCreator(null));

        return products;
    }

    @Override
    public List<Product> getAllActiveProductsByCategoryId(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category != null) {
            List<Product> products = productRepository.getAllActiveProductsByCategory(category);
            products.forEach(p -> p.setCreator(null));
            return products;
        }else{
            throw new AdminException("Category with id " + categoryId + " doesn't exist.");
        }
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product getPublicProductById(Long id) {
        Product product = productRepository.getActiveProduct(id);
        product.setCreator(null);

        return product;
    }

    @Override
    public void deleteProduct(Long productId) throws AdminException {
        Product product = getProductById(productId);
        if (product != null) {
            productRepository.delete(product);
        }else{
            throw new AdminException("Cannot delete product with id " + productId + ". Product doesn't exist.");
        }
    }

    @Override
    public void activateProduct(Long productId) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setActive(!product.getActive());
            productRepository.save(product);
        }else{
            throw new AdminException("Cannot activate/deactivate product. Product with id " + productId + " doesn't exist.");
        }
    }
}
