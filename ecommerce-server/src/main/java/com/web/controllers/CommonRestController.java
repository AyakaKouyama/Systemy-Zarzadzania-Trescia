package com.web.controllers;

import com.ecommerce.data.dtos.ContactEmailDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.EmailException;
import com.web.services.DataService;
import com.web.services.EmailService;
import com.web.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonRestController extends BasicController {

    private final DataService dataService;
    private final FileService fileService;
    private final EmailService emailService;

    @Autowired
    public CommonRestController(DataService dataService, FileService fileService, EmailService emailService){
        this.dataService = dataService;
        this.fileService = fileService;
        this.emailService = emailService;
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories(){
        return dataService.getAllCategories();
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(){
        List<Product> products = dataService.getAllPublicProducts();
        for(Product product : products){
            product.setStringImages(fileService.decodeImages(product.getImages()));
        }

        return products;
    }

    @GetMapping("/products/category/{id}")
    public List<Product> getAllProductsById(@PathVariable("id")String categoryId){
        List<Product> products = dataService.getAllPublicProductsbyCategoryId(categoryId);
        for(Product product : products){
            product.setStringImages(fileService.decodeImages(product.getImages()));
        }

        return products;
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable("id")String categoryId){
        Product product = dataService.getPublicProductbyId(categoryId);
        product.setStringImages(fileService.decodeImages(product.getImages()));

        return product;
    }

    @PostMapping("/contact/send")
    public void sendContactMessage(@RequestBody ContactEmailDto message) throws EmailException {
        emailService.sendMessage(message.getEmail(), message.getMessage());
    }
}
