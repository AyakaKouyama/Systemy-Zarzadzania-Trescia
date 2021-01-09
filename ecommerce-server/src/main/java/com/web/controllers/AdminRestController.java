package com.web.controllers;

import com.ecommerce.data.dtos.CreateProductDto;
import com.ecommerce.data.dtos.FileDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Image;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.exceptions.FileException;
import com.web.services.DataService;
import com.web.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/product")
    public void createProduct(@RequestParam(value = "file", required = false) byte[] files, @RequestParam(value = "fileName", required = false) List<String> filesNames, @RequestParam("name")String name, @RequestParam(value = "my-select[]", required = false)
            List<String> categories,  @RequestParam("sku")String sku,  @RequestParam("short-desc")String shortDesc,  @RequestParam("desc")String desc,  @RequestParam("price")
            BigDecimal price,  @RequestParam(value = "qty", required = false) BigInteger qty, @RequestParam(value = "userLogin") String userLogin)
            throws IOException, FileException {
        Product product = dataService.createProduct(name, sku, shortDesc, desc, price, qty, categories, userLogin);
        fileService.saveFile(Collections.singletonList(files), filesNames, product);
    }


    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return dataService.getAllProducts();
    }

    @GetMapping("/product/{productId}")
    public Product getProductById(@PathVariable("productId") String productId) {
        return dataService.getProductById(productId);
    }

    @PostMapping("/image/decode")
    public List<String> decodeImages(@RequestBody List<Image> images) {
        return fileService.decodeImages(images);
    }



}
