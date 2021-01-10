package com.web.controller;

import com.ecommerce.data.entities.Product;
import com.ecommerce.data.services.UserService;
import com.web.services.ExchangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.util.List;

@Controller
public class PublicPagesController extends BasicController {

    private final String commonUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public PublicPagesController(@Value("${app.service.common}") String commonUrl, RestTemplate restTemplate){
        this.commonUrl = commonUrl;
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/")
    public String index(ModelMap map) {
        List<Product> products = ExchangeUtils.exchangeData(commonUrl, "products", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, null);
        List<Product> categories = ExchangeUtils.exchangeData(commonUrl, "categories", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, null);
        map.put("products", products);
        map.put("categories", categories);
        return "index";
    }

    @RequestMapping(value = "/products/category/{id}")
    public String getProductsByCategory(ModelMap map, @PathVariable("id")String categoryId) {
        List<Product> products = ExchangeUtils.exchangeData(commonUrl, "products/category/" + categoryId, HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, null);
        List<Product> categories = ExchangeUtils.exchangeData(commonUrl, "categories", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, null);
        map.put("products", products);
        map.put("categories", categories);
        return "index";
    }
}
