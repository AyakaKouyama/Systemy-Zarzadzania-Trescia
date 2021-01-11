package com.web.controllers;

import com.ecommerce.data.dtos.CreateProductDto;
import com.ecommerce.data.dtos.FileDto;
import com.ecommerce.data.dtos.ProductDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Image;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.exceptions.ApiException;
import com.ecommerce.data.exceptions.FileException;
import com.web.services.DataService;
import com.web.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController extends BasicController {

    private final DataService dataService;
    private final FileService fileService;

    @Autowired
    public AdminRestController(DataService dataService, FileService fileService){
        this.dataService = dataService;
        this.fileService = fileService;
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories(){
        return dataService.getAllCategories();
    }

    @PostMapping("/category")
    public void createCategory(@RequestBody String categoryName){
       dataService.createCategory(categoryName);
    }

    @GetMapping("/category/{categoryId}")
    public Category getCategoryById(@PathVariable(value = "categoryId") String categoryId) throws AdminException {
        return dataService.getCategoryById(Long.parseLong(categoryId));
    }

    @DeleteMapping("/category/{categoryId}")
    public void deleteCategory(@PathVariable(value = "categoryId") String categoryId) throws AdminException{
        dataService.deleteCategory(categoryId);
    }

    @PostMapping("/category/{categoryId}/activate")
    public void activateCategory(@PathVariable("categoryId") String categoryId, @Param("active")Boolean active) {
        dataService.activateCategory(categoryId, active);
    }


    @PostMapping(value = "/product")
    public void createProduct(@RequestBody CreateProductDto createProductDto)
            throws IOException, FileException {
        int i = 0;
        ProductDto productDto = createProductDto.getProductDto();
        Product product = dataService.createProduct(productDto.getName(), productDto.getSku(), productDto.getShortDescription(), productDto.getDescription(), productDto.getPrice(), productDto.getQty(), productDto.getCategories(), productDto.getUser().getLogin());
        fileService.saveFile(createProductDto.getFiles(), product);
    }


    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return dataService.getAllProducts();
    }

    @GetMapping("/product/{productId}")
    public Product getProductById(@PathVariable("productId") String productId) {
        return dataService.getProductById(productId);
    }

    @DeleteMapping("/product/{productId}")
    public void deleteProduct(@PathVariable("productId") String productId) {
        dataService.deleteProduct(productId);
    }

    @PostMapping("/product/{productId}/activate")
    public void deleteProduct(@PathVariable("productId") String productId, @Param("active")Boolean active) {
        dataService.activateProduct(productId, active);
    }

    @PostMapping("/image/decode")
    public List<String> decodeImages(@RequestBody List<Image> images) {
        return fileService.decodeImages(images);
    }



}
