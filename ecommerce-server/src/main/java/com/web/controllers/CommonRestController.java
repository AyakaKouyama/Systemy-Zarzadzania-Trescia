package com.web.controllers;

import com.ecommerce.data.dtos.ContactEmailDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.EmailException;
import com.web.services.DataService;
import com.web.services.EmailService;
import com.web.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonRestController extends BasicController {

    private final DataService dataService;
    private final FileService fileService;
    private final EmailService emailService;

    @Autowired
    public CommonRestController(DataService dataService, FileService fileService, EmailService emailService) {
        this.dataService = dataService;
        this.fileService = fileService;
        this.emailService = emailService;
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return dataService.getAllPublicCategories();
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(@RequestParam(value = "category", required = false) Long id) {
        List<Product> products = null;
        if(id != null){
            products = dataService.getAllPublicProductsbyCategoryId(id);
        }else {
            products = dataService.getAllPublicProducts();
        }

        if(products != null) {
            for (Product product : products) {
                product.setStringImages(fileService.decodeImages(product.getImages()));
            }
        }

        return products;
    }


    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable("id") Long categoryId) {
        Product product = dataService.getPublicProductbyId(categoryId);
        product.setStringImages(fileService.decodeImages(product.getImages()));

        return product;
    }

    @PostMapping("/contact/send")
    public void sendContactMessage(@RequestBody ContactEmailDto message) throws EmailException {
        log.info("Send email message to: {}", message.getEmail());
        log.info("Send email message: {}", message.getMessage());

        emailService.sendMessage(message.getEmail(), message.getMessage());
    }
}
