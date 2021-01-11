package com.web.controller;

import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.services.UserService;
import com.web.services.ExchangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public String index(ModelMap map, @RequestParam(value = "category", required = false) String categoryId) {
        List<Product> products = null;
        if(categoryId != null && !categoryId.equals("") && !categoryId.equals("-1")){
            products = ExchangeUtils.exchangeData(commonUrl, "products/category/" + categoryId, HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, null, null);
        }else {
            products = ExchangeUtils.exchangeData(commonUrl,
                    "products",
                    HttpMethod.GET,
                    new ParameterizedTypeReference<List<Product>>() {},
                    restTemplate,
                    null,
                    null);
        }


        List<Category> categories = ExchangeUtils.exchangeData(commonUrl, "categories", HttpMethod.GET, new ParameterizedTypeReference<List<Category>>() {}, restTemplate, null, null);
        map.put("products", products);
        map.put("categories", categories);

        Category current = null;
        if(categoryId != null && !categoryId.equals("")){
            List<Category> filter = categories.stream().filter(f -> f.getId().equals(Long.parseLong(categoryId))).collect(Collectors.toList());
            if(filter.size() > 0) current = filter.get(0);
            categories.remove(current);
        }
        map.put("currentCategory", current);
        return "index";
    }

    @RequestMapping(value = "/product/{id}")
    public String productDetails(ModelMap map, @PathVariable("id")String productId){
        Product product = ExchangeUtils.exchangeData(commonUrl, "products/" + productId, HttpMethod.GET, new ParameterizedTypeReference<Product>() {}, restTemplate, null, null);

        map.put("product", product);
        return "main-product";
    }
}