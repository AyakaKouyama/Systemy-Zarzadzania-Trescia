package com.web.controller;

import com.ecommerce.data.dtos.CategoryDto;
import com.ecommerce.data.dtos.CreateProductDto;
import com.ecommerce.data.dtos.FileDto;
import com.ecommerce.data.dtos.ProductDto;
import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
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

    @RequestMapping(value = "/product/{id}/delete")
    public String deleteProduct(@PathVariable("id")String productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        ExchangeUtils.exchangeData(adminUrl, "product/" + productId, HttpMethod.DELETE, new ParameterizedTypeReference<Product>() {}, restTemplate, null, token);
        map.put("products", getProducts(token));
        return "products";
    }

    @RequestMapping(value = "/product/{id}/activate")
    public String activateProduct(@PathVariable("id")String productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token, @Param("active")Boolean active) {
        Map<String, String> params = new HashMap<>();
        params.put("active", active.toString());

        ExchangeUtils.exchangeData(adminUrl, "product/" + productId + "/activate", HttpMethod.POST, new ParameterizedTypeReference<Product>() {}, restTemplate, params, token);
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
    public String editCategory(@PathVariable("id")String categoryId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token, @Param("active")Boolean active) {
        Category category =  ExchangeUtils.exchangeData(adminUrl, "category/" + categoryId, HttpMethod.GET, new ParameterizedTypeReference<Category>() {}, restTemplate, null, token);
        map.put("category", category);
        return "edit-category";
    }

    @GetMapping(value = "/category/{id}/activate")
    public String activateCategory(@PathVariable("id")String categoryId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token, @Param("active")Boolean active) {
        Map<String, String> params = new HashMap<>();
        params.put("active", active.toString());

        ExchangeUtils.exchangeData(adminUrl, "category/" + categoryId + "/activate", HttpMethod.POST, new ParameterizedTypeReference<Product>() {}, restTemplate, params, token);
        map.put("categories",getCategories(token));
        return "categories";
    }

    @RequestMapping(value = "/category/{id}/delete")
    public String deleteCategory(@PathVariable("id")String productId, ModelMap map, @CookieValue(value = "token", defaultValue = "")String token) {
        try {
            ExchangeUtils.exchangeData(adminUrl,
                    "category/" + productId,
                    HttpMethod.DELETE,
                    new ParameterizedTypeReference<Product>() {},
                    restTemplate,
                    null,
                    token);
        }catch (ApiException exc){
            map.put("error", exc.getMessage());
        }

        map.put("categories",getCategories(token));
        return "categories";
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

        List<FileDto> filesDto = new ArrayList<>();
        List<String> names = new ArrayList<>();
        files.forEach(f -> {
            try {
                filesDto.add(new FileDto(new String(Base64.getEncoder().encode(f.getBytes())), f.getOriginalFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            names.add(f.getOriginalFilename());

        });


      /*  MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.put("file", new ArrayList<>(filesDto));
        data.put("fileName", new ArrayList<>(names));
        data.add("name", name);
        data.add("sku", sku);
        data.add("short-desc", shortDesc);
        data.add("desc", desc);
        data.add("price", price);
        data.add("qty", qty);
        data.put("my-select[]", new ArrayList<>(categories));
        data.add("userLogin", getCurrentUser(token).getLogin()); */

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

        createProductDto.setProductDto(productDto);

        ExchangeUtils.postData(adminUrl, "product", CreateProductDto.class, restTemplate, null,createProductDto, token);

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
