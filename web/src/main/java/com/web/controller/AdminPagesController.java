package com.web.controller;

import com.ecommerce.data.dtos.CategoryDto;
import com.ecommerce.data.dtos.CreateProductDto;
import com.ecommerce.data.dtos.FileDto;
import com.ecommerce.data.dtos.ProductDto;
import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Image;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.exceptions.ApiException;
import com.ecommerce.data.exceptions.FileException;
import com.web.services.ExchangeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminPagesController extends BasicController{
        private final String adminUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public AdminPagesController(@Value("${app.service.admin}") String adminUrl, RestTemplate restTemplate){
        this.adminUrl = adminUrl;
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/main")
    public String adminMainPage() {
        return "admin-main";
    }

    @RequestMapping(value = "/products")
    public String adminProducts(ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        map.put("products", getProducts(token));
        return "products";
    }

    @RequestMapping(value = "/add/product")
    public String addProduct(ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        map.put("categories", getCategories(token).stream().filter(Category::getActive).collect(Collectors.toList()));
        return "add-product";
    }

    @RequestMapping(value = "/product/{id}")
    public String editProduct(@PathVariable("id")Long productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {

        Product product =  ExchangeUtils.exchangeData(adminUrl, "products/" + productId, HttpMethod.GET, new ParameterizedTypeReference<Product>() {}, restTemplate, null, token);
        map.put("product", product);
        map.put("categoriesNames", product.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        map.put("categories", getCategories(token));
        if(!CollectionUtils.isEmpty(product.getImages())) {
            List<Image> images = new ArrayList<>();
            List<String> imgData = ExchangeUtils.postListData(adminUrl,
                    "images/decode",
                    restTemplate,
                    null,
                    product.getImages(),
                    token);

            int c = 0;
            for(String img : imgData){
                Image i = new Image();
                i.setStringData(img);
                i.setFileName(product.getImages().get(c).getFileName());
                i.setId(product.getImages().get(c).getId());
                images.add(i);
            }

            map.put("images", images);
        }
        return "edit-product";
    }

    @RequestMapping(value = "/product/{id}/delete")
    public String deleteProduct(@PathVariable("id")Long productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        ExchangeUtils.exchangeData(adminUrl, "products/" + productId, HttpMethod.DELETE, new ParameterizedTypeReference<Product>() {}, restTemplate, null, token);
        map.put("products", getProducts(token));
        return "products";
    }

    @RequestMapping(value = "/product/{id}/activate")
    public String activateProduct(@PathVariable("id")Long productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token, @Param("active")Boolean active) {
        Map<String, String> params = new HashMap<>();
        params.put("active", active.toString());

        ExchangeUtils.exchangeData(adminUrl, "products/" + productId + "/activate", HttpMethod.POST, new ParameterizedTypeReference<Product>() {}, restTemplate, params, token);
        map.put("products", getProducts(token));
        return "products";
    }

    @RequestMapping(value = "/categories")
    public String adminCategories(ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        map.put("categories",getCategories(token));
        return "categories";
    }

    @RequestMapping(value = "/add/category")
    public String addCategory() {
        return "add-category";
    }

    @RequestMapping(value = "/category/{id}")
    public String editCategory(@PathVariable("id")Long categoryId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token, @Param("active")Boolean active) {
        Category category =  ExchangeUtils.exchangeData(adminUrl, "categories/" + categoryId, HttpMethod.GET, new ParameterizedTypeReference<Category>() {}, restTemplate, null, token);
        map.put("category", category);
        return "edit-category";
    }

    @GetMapping(value = "/category/{id}/activate")
    public String activateCategory(@PathVariable("id")Long categoryId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token, @Param("active")Boolean active) {
        Map<String, String> params = new HashMap<>();
        params.put("active", active.toString());

        ExchangeUtils.exchangeData(adminUrl, "categories/" + categoryId + "/activate", HttpMethod.POST, new ParameterizedTypeReference<Product>() {}, restTemplate, params, token);
        map.put("categories",getCategories(token));
        return "categories";
    }

    @RequestMapping(value = "/category/{id}/delete")
    public String deleteCategory(@PathVariable("id")Long productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        try {
            ExchangeUtils.exchangeData(adminUrl,
                    "categories/" + productId,
                    HttpMethod.DELETE,
                    new ParameterizedTypeReference<Product>() {},
                    restTemplate,
                    null,
                    token);
        }catch (ApiException exc){
            if(exc.getMessage().equals("Cannot delete category that has assigned products.")) {
                map.put("error", "Nie można usunąć kategorii, która posiada przypisane produkty.");
            }else{
                map.put("error", "Wystąpił nieznany błąd. Spróbuj ponownie później.");
            }
        }

        map.put("categories",getCategories(token));
        return "categories";
    }

    @RequestMapping(value = {"/submit/category", "/submit/category/{id}"}, consumes = {"multipart/form-data"})
    public String submitCategory(@RequestParam("name") String name, @CookieValue(value = "token", defaultValue = "")String token, @PathVariable(value = "id", required = false) Long id, ModelMap map) {
        CategoryDto category = new CategoryDto();
        category.setName(name);
        category.setId(id);

        try {
            ExchangeUtils.postData(adminUrl, "categories", CategoryDto.class, restTemplate, null, category, token);
        }catch (ApiException exception){
            map.put("error", "Kategoria o podanej nazwie już istnieje.");

            if(id != null){
                Category cat =  ExchangeUtils.exchangeData(adminUrl, "categories/" + id, HttpMethod.GET, new ParameterizedTypeReference<Category>() {}, restTemplate, null, token);
                map.put("category", cat);
                return "edit-category";
            }else{
                return "add-category";
            }
        }

        map.put("categories",getCategories(token));
        return "categories";
    }

    @RequestMapping(value = {"/submit/product", "/submit/product/{id}"}, consumes = {"multipart/form-data"})
    public String submitProduct(@PathVariable(value = "id", required = false) Long id, @RequestParam(value = "file", required = false) List<MultipartFile> files, @RequestParam(value = "name", required = true)String name, @RequestParam(value = "my-select[]", required = false)
            List<String> categories,  @RequestParam(value = "sku", required = false)String sku,  @RequestParam(value = "short-desc", required = false)String shortDesc,  @RequestParam(value = "desc", required = false)String desc,  @RequestParam(value = "price", required = false)BigDecimal price,  @RequestParam(value = "qty", required = false)BigInteger qty, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token)
            throws IOException, FileException {

        List<FileDto> filesDto = new ArrayList<>();
        if(files != null) {

            files.forEach(f -> {
                try {
                    if(!f.getOriginalFilename().equals("") && !f.getOriginalFilename().endsWith(".jpg")){
                        map.put("error", "Niedozwolony format plików. Akceptowane formaty: jpg.");
                        return;
                    }else if(f.getSize() > 1000000L){
                        map.put("error", "Maksymalny rozmiar plików to 1 MB.");
                        return;
                    }

                    filesDto.add(new FileDto(new String(Base64.getEncoder().encode(f.getBytes())),
                            f.getOriginalFilename()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (!map.containsAttribute("error")) {
            CreateProductDto createProductDto = new CreateProductDto();
            createProductDto.setFiles(filesDto);

            ProductDto productDto = new ProductDto();
            productDto.setName(name);
            productDto.setSku(sku);
            productDto.setShortDescription(shortDesc);
            productDto.setDescription(desc);
            productDto.setPrice(price);
            productDto.setQty(qty);
            productDto.setCategories(categories);
            productDto.setUser(new UserDto(getCurrentUser(token).getLogin()));
            productDto.setId(id);
            createProductDto.setProductDto(productDto);

            try {
                ExchangeUtils.postData(adminUrl,
                        "products",
                        CreateProductDto.class,
                        restTemplate,
                        null,
                        createProductDto,
                        token);
            }catch (ApiException exc){
                if(exc.getMessage().equalsIgnoreCase("Product with this name already exists.")) {
                    map.put("error", "Produkt o podanej nazwie już istnieje.");
                }
            }

        }

        if(map.containsAttribute("error")){
            if(id != null) {
                Product product = ExchangeUtils.exchangeData(adminUrl,
                        "products/" + id,
                        HttpMethod.GET,
                        new ParameterizedTypeReference<Product>() {},
                        restTemplate,
                        null,
                        token);
                map.put("product", product);
                map.put("categoriesNames",
                        product.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
                map.put("categories", getCategories(token));
                if (!CollectionUtils.isEmpty(product.getImages())) {
                    List<Image> images = new ArrayList<>();
                    List<String> imgData = ExchangeUtils.postListData(adminUrl,
                            "images/decode",
                            restTemplate,
                            null,
                            product.getImages(),
                            token);

                    int c = 0;
                    for (String img : imgData) {
                        Image i = new Image();
                        i.setStringData(img);
                        i.setFileName(product.getImages().get(c).getFileName());
                        i.setId(product.getImages().get(c).getId());
                        images.add(i);
                    }

                    map.put("images", images);
                }
                return "edit-product";
            }else{
                map.put("categories", getCategories(token).stream().filter(Category::getActive).collect(Collectors.toList()));
                return "add-product";
            }
        }

        map.put("products", getProducts(token));
        return "products";
    }

    @RequestMapping("/image/delete/{id}")
    @ResponseBody
    public String deleteImage(@PathVariable(value = "id")Long id, @CookieValue(value = "token", defaultValue = "")String token){
        try {
            ExchangeUtils.exchangeData(adminUrl,
                    "images/" + id,
                    HttpMethod.DELETE,
                    new ParameterizedTypeReference<List<Category>>() {},
                    restTemplate,
                    null,
                    token);
        }catch (Exception e) {
            return "error";
        }

        return "ok";
    }

    private  List<Category> getCategories(String token){
        return ExchangeUtils.exchangeData(adminUrl, "categories", HttpMethod.GET, new ParameterizedTypeReference<List<Category>>() {}, restTemplate, null, token);
    }

    private List<Product> getProducts(String token){
        return ExchangeUtils.exchangeData(adminUrl, "products", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, token);
    }



}
