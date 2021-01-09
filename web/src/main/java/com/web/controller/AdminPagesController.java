package com.web.controller;

import com.ecommerce.data.dtos.CategoryDto;
import com.ecommerce.data.dtos.CreateProductDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.FileException;
import com.web.services.ExchangeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
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
import java.util.Collections;
import java.util.List;
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
    public String adminMainPage(@CookieValue(value = "token", defaultValue = "")String token) {
        return "admin-main";
    }

    @RequestMapping(value = "/products")
    public String adminProducts(ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        map.put("products", getProducts(token));
        return "products";
    }

    @RequestMapping(value = "/add/product")
    public String addProduct(ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        map.put("categories", getCategories(token));
        return "add-product";
    }

    @RequestMapping(value = "/product/{id}")
    public String editProduct(@PathVariable("id")String productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {

        Product product =  ExchangeUtils.exchangeData(adminUrl, "product/" + productId, HttpMethod.GET, new ParameterizedTypeReference<Product>() {}, restTemplate, null, token);
        map.put("product", product);
        map.put("categoriesNames", product.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        map.put("categories", getCategories(token));
        if(!CollectionUtils.isEmpty(product.getImages()))
            map.put("images", ExchangeUtils.postListData(adminUrl, "image/decode", restTemplate, null, product.getImages(), token));
        return "edit-product";
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
    public String editCategory(@PathParam("id")String productId) {
        return "edit-category";
    }

    @RequestMapping(value = "/submit/category")
    @ResponseBody
    public void submitCategory(@RequestBody CategoryDto category, @CookieValue(value = "token", defaultValue = "")String token) {
        ExchangeUtils.postData(adminUrl, "category", Product.class, restTemplate, null, category.getName(), token);
    }

    @RequestMapping(value = "/submit/product", consumes = {"multipart/form-data"})
    public String submitProduct(@RequestParam("file") List<MultipartFile> files, @RequestParam("name")String name, @RequestParam(value = "my-select[]", required = false)
            List<String> categories,  @RequestParam("sku")String sku,  @RequestParam("short-desc")String shortDesc,  @RequestParam("desc")String desc,  @RequestParam("price")BigDecimal price,  @RequestParam(value = "qty", required = false)BigInteger qty, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token)
            throws IOException, FileException {

        List<ByteArrayResource> filesDto = new ArrayList<>();
        List<String> names = new ArrayList<>();
        files.forEach(f -> {
            try {
                filesDto.add(new ByteArrayResource(f.getBytes()));
                names.add(f.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        MultiValueMap<String, Object> data = new LinkedMultiValueMap();
        data.put("file", Collections.singletonList(files.get(0).getBytes()));
        data.put("fileName", new ArrayList<>(names));
        data.put("name", Collections.singletonList(name));
        data.put("sku", Collections.singletonList(sku));
        data.put("short-desc", Collections.singletonList(shortDesc));
        data.put("desc", Collections.singletonList(desc));
        data.put("price", Collections.singletonList(price));
        data.put("qty", Collections.singletonList(qty));
        data.put("my-select[]", new ArrayList<>(categories));
        data.put("userLogin", Collections.singletonList(getCurrentUser(token).getLogin()));

        ExchangeUtils.multipartPostData(adminUrl, "product", CreateProductDto.class, restTemplate, null, data, token);

       // fileService.saveFile(files, product);

        map.put("products", getProducts(token));
        return "products";
    }


    private  List<Category> getCategories(String token){
        return ExchangeUtils.exchangeData(adminUrl, "categories", HttpMethod.GET, new ParameterizedTypeReference<List<Category>>() {}, restTemplate, null, token);
    }

    private List<Product> getProducts(String token){
        return ExchangeUtils.exchangeData(adminUrl, "products", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, token);
    }



}
