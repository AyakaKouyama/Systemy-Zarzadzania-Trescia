package com.web.controllers;

import com.ecommerce.data.dtos.CategoryDto;
import com.ecommerce.data.dtos.CreateProductDto;
import com.ecommerce.data.dtos.ProductDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Image;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.exceptions.FileException;
import com.web.services.DataService;
import com.web.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController extends BasicController {

    private final DataService dataService;
    private final FileService fileService;

    @Autowired
    public AdminRestController(DataService dataService, FileService fileService) {
        this.dataService = dataService;
        this.fileService = fileService;
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return dataService.getAllCategories();
    }

    @PostMapping("/categories")
    public void createCategory(@RequestBody CategoryDto categoryDto) throws AdminException {
        dataService.createCategory(categoryDto.getName(), categoryDto.getId());
    }

    @GetMapping("/categories/{categoryId}")
    public Category getCategoryById(@PathVariable(value = "categoryId") Long categoryId) throws AdminException {
        return dataService.getCategoryById(categoryId);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategory(@PathVariable(value = "categoryId") Long categoryId) throws AdminException {
        dataService.deleteCategory(categoryId);
    }

    @PostMapping("/categories/{categoryId}/activate")
    public void activateCategory(@PathVariable("categoryId") Long categoryId) {
        dataService.activateCategory(categoryId);
    }


    @PostMapping(value = "/products")
    public void createProduct(@RequestBody CreateProductDto createProductDto)
            throws IOException, FileException {
        ProductDto productDto = createProductDto.getProductDto();
        Product product = dataService.createProduct(productDto.getName(),
                productDto.getSku(),
                productDto.getShortDescription(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.getQty(),
                productDto.getCategories(),
                productDto.getUser().getLogin(),
                productDto.getId());
        fileService.saveFile(createProductDto.getFiles(), product);
    }


    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return dataService.getAllProducts();
    }

    @GetMapping("/products/{productId}")
    public Product getProductById(@PathVariable("productId")Long productId) {
        return dataService.getProductById(productId);
    }

    @DeleteMapping("/products/{productId}")
    public void deleteProduct(@PathVariable("productId") Long productId) throws AdminException {
        dataService.deleteProduct(productId);
    }

    @PostMapping("/products/{productId}/activate")
    public void activateProduct(@PathVariable("productId") Long productId) {
        dataService.activateProduct(productId);
    }

    @PostMapping("/images/decode")
    public List<String> decodeImages(@RequestBody List<Image> images) {
        return fileService.decodeImages(images);
    }

    @DeleteMapping("/images/{id}")
    private void deleteImage(@PathVariable("id") Long id) throws AdminException {
        dataService.deleteImage(id);
    }
}
