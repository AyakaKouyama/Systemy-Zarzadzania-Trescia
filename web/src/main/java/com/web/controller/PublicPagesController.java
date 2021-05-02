package com.web.controller;

import com.ecommerce.data.dtos.Buyer;
import com.ecommerce.data.dtos.ContactEmailDto;
import com.ecommerce.data.dtos.OrderDetails;
import com.ecommerce.data.dtos.OrderFormDto;
import com.ecommerce.data.dtos.PayUProduct;
import com.ecommerce.data.entities.Cart;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.services.UserService;
import com.web.services.ExchangeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class PublicPagesController extends BasicController {

    private final String commonUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public PublicPagesController(@Value("${app.service.common}") String commonUrl, RestTemplate restTemplate){
        this.commonUrl = commonUrl;
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/signout")
    public String logout(HttpServletRequest request, HttpServletResponse response, ModelMap map, @RequestParam(value = "category", required = false) String categoryId){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equalsIgnoreCase("token")){
                    cookie.setPath("/");
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        List<Product> products = null;
        if(categoryId != null && !categoryId.equals("") && !categoryId.equals("-1")){
            Map<String, String> params = new HashMap();
            params.put("category", categoryId);
            products = ExchangeUtils.exchangeData(commonUrl, "products", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, params, null);
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

    @RequestMapping(value = "/contact")
    public String getContactPage(){
        return "contact";
    }

    @RequestMapping(value = "/")
    public String index(ModelMap map, @RequestParam(value = "category", required = false) String categoryId) {
        List<Product> products = null;
        if(categoryId != null && !categoryId.equals("") && !categoryId.equals("-1")){
            Map<String, String> params = new HashMap();
            params.put("category", categoryId);
            products = ExchangeUtils.exchangeData(commonUrl, "products", HttpMethod.GET, new ParameterizedTypeReference<List<Product>>() {}, restTemplate, params, null);
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
    public String productDetails(ModelMap map, @PathVariable("id")Long productId){
        Product product = ExchangeUtils.exchangeData(commonUrl, "products/" + productId, HttpMethod.GET, new ParameterizedTypeReference<Product>() {}, restTemplate, null, null);

        map.put("product", product);
        return "main-product";
    }


    @PostMapping(value = "/contact/send")
    public String sendEContactEmail(ModelMap map, @ModelAttribute ContactEmailDto message){
        try{
            log.info("Send email message to: {}", message.getEmail());
            log.info("Send email message: {}", message.getMessage());

            ExchangeUtils.postData(commonUrl, "contact/send", ContactEmailDto.class, restTemplate, null, message, null);
            map.put("response", "Twoja wiadomosć została wysłana. Dziękujemy!");
        }catch (Exception e) {
            map.put("response", "Wystąpił błąd podczas wysyłania wiadomości. Spróbuj ponownie później.");
        }

        return "contact";
    }

    @GetMapping(value = "/cart/{id}")
    public String addToCart(@PathVariable("id")String productId, ModelMap map){
        log.info("Adding product to cart");
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);

        Cart cart = ExchangeUtils.exchangeData(commonUrl, "/cart/" + productId,  HttpMethod.GET, new ParameterizedTypeReference<Cart>() {}, restTemplate, params, null);

        BigDecimal sumQty = BigDecimal.ZERO;
        BigDecimal sumPrice = BigDecimal.ZERO;

        if(cart != null) {
            Map<Long, Product> productsMap = new HashMap();
            for (Product product : cart.getProducts()) {
                if (!productsMap.containsKey(product.getId())) {
                    product.setQty(BigDecimal.ONE);
                    product.setSum(product.getPrice());
                    productsMap.put(product.getId(), product);
                } else {
                    productsMap.get(product.getId())
                            .setQty(productsMap.get(product.getId()).getQty().add(BigDecimal.ONE));
                    productsMap.get(product.getId())
                            .setSum(product.getPrice().multiply(productsMap.get(product.getId()).getQty()));
                }
            }


            for (Product product : productsMap.values()) {
                sumQty = sumQty.add(product.getQty());
                sumPrice = sumPrice.add(product.getSum());
            }

            map.put("products", new ArrayList<>(productsMap.values()));
            map.put("sumQty", sumQty);
            map.put("sumPrice", sumPrice);
        }

        return "cart";
    }

    @GetMapping(value = "/cart")
    public String getCart(ModelMap map){
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);

        Cart cart = ExchangeUtils.exchangeData(commonUrl, "/cart",  HttpMethod.GET, new ParameterizedTypeReference<Cart>() {}, restTemplate, params, null);

        BigDecimal sumQty = BigDecimal.ZERO;
        BigDecimal sumPrice = BigDecimal.ZERO;

        if(cart != null) {
            Map<Long, Product> productsMap = new HashMap();
            for (Product product : cart.getProducts()) {
                if (!productsMap.containsKey(product.getId())) {
                    product.setQty(BigDecimal.ONE);
                    product.setSum(product.getPrice());
                    productsMap.put(product.getId(), product);
                } else {
                    productsMap.get(product.getId())
                            .setQty(productsMap.get(product.getId()).getQty().add(BigDecimal.ONE));
                    productsMap.get(product.getId())
                            .setSum(product.getPrice().multiply(productsMap.get(product.getId()).getQty()));
                }
            }


            for (Product product : productsMap.values()) {
                sumQty = sumQty.add(product.getQty());
                sumPrice = sumPrice.add(product.getSum());
            }

            map.put("products", new ArrayList<>(productsMap.values()));
            map.put("sumQty", sumQty);
            map.put("sumPrice", sumPrice);
        }

        return "cart";
    }

    @GetMapping(value = "/payment-step/1")
    public String paymentStepOne(){
        return "step-one";
    }

    @GetMapping(value = "/order-confirm")
    public String orderConfirmPage(){
        return "order-confirm";
    }

    @PostMapping(value = "create-order")
    public String createOrder(@ModelAttribute OrderFormDto orderFormDto, @CookieValue(value = "ref", defaultValue = "")String refLink){
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);

        if(!StringUtils.isEmpty(refLink)){
            params.put("refLink", refLink);
        }

        Cart cart = ExchangeUtils.exchangeData(commonUrl, "/cart",  HttpMethod.GET, new ParameterizedTypeReference<Cart>() {}, restTemplate, params, null);

        BigDecimal sumQty = BigDecimal.ZERO;
        BigDecimal sumPrice = BigDecimal.ZERO;

        Map<Long, Product> productsMap = new HashMap();
        for(Product product : cart.getProducts()){
            if(!productsMap.containsKey(product.getId())){
                product.setQty(BigDecimal.ONE);
                product.setSum(product.getPrice());
                productsMap.put(product.getId(), product);


            }else{
                productsMap.get(product.getId()).setQty(productsMap.get(product.getId()).getQty().add(BigDecimal.ONE));
                productsMap.get(product.getId()).setSum(product.getPrice().multiply(productsMap.get(product.getId()).getQty()));
            }
        }


        for(Product product : productsMap.values()){
            sumQty = sumQty.add(product.getQty());
            sumPrice = sumPrice.add(product.getSum());
        }


        BigDecimal multiplier = new BigDecimal(100);

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setDescription("Zakupy w biurze podróży Wakacje Marzeń");
        orderDetails.setTotalAmount(sumPrice.multiply(multiplier).setScale(0, BigDecimal.ROUND_HALF_UP).toString());

        Buyer buyer = new Buyer();
        buyer.setEmail(orderFormDto.getEmail());
        buyer.setPhone(orderFormDto.getPhone());
        buyer.setFirstName(orderFormDto.getName());
        buyer.setLastName(orderFormDto.getSurrname());

        orderDetails.setBuyer(buyer);

        for(Product product : productsMap.values()){
            PayUProduct payUProduct = new PayUProduct();
            payUProduct.setName(product.getName());
            payUProduct.setUnitPrice(product.getSum().multiply(multiplier).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
            payUProduct.setQuantity(product.getQty().toString());

            orderDetails.getProducts().add(payUProduct);
        }

        String ipAddress = ((WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getRemoteAddress();
        orderDetails.setCustomerIp(ipAddress);

        orderDetails.setAddress(orderFormDto.getStreet());
        orderDetails.setCity(orderFormDto.getCity());
        orderDetails.setPostalCode(orderFormDto.getPostalCode());
        orderDetails.setSessionId(sessionId);

        String redirectUri = ExchangeUtils.postData(commonUrl, "create/order", String.class, restTemplate, params, orderDetails, null);

        return "redirect:" + redirectUri;
    }

    @GetMapping(value = "/cart/{id}/delete")
    public String deleteProductFromCart(@PathVariable("id")String productId, ModelMap map){
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        Cart cart = ExchangeUtils.exchangeData(commonUrl, "/cart/delete/" + productId,  HttpMethod.GET, new ParameterizedTypeReference<Cart>() {}, restTemplate, params, null);

        BigDecimal sumQty = BigDecimal.ZERO;
        BigDecimal sumPrice = BigDecimal.ZERO;

        if(cart != null) {
            Map<Long, Product> productsMap = new HashMap();
            for (Product product : cart.getProducts()) {
                if (!productsMap.containsKey(product.getId())) {
                    product.setQty(BigDecimal.ONE);
                    product.setSum(product.getPrice());
                    productsMap.put(product.getId(), product);
                } else {
                    productsMap.get(product.getId())
                            .setQty(productsMap.get(product.getId()).getQty().add(BigDecimal.ONE));
                    productsMap.get(product.getId())
                            .setSum(product.getPrice().multiply(productsMap.get(product.getId()).getQty()));
                }
            }


            for (Product product : productsMap.values()) {
                sumQty = sumQty.add(product.getQty());
                sumPrice = sumPrice.add(product.getSum());
            }

            map.put("products", new ArrayList<>(productsMap.values()));
            map.put("sumQty", sumQty);
            map.put("sumPrice", sumPrice);
        }

        return "cart";
    }



}