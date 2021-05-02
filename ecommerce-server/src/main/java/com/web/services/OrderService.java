package com.web.services;

import com.ecommerce.data.dtos.OrderDetails;
import com.ecommerce.data.dtos.OrderResponse;
import com.ecommerce.data.dtos.PayUToken;
import com.ecommerce.data.entities.Cart;
import com.ecommerce.data.entities.Order;
import com.ecommerce.data.entities.OrderStatus;
import com.ecommerce.data.entities.Partner;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.services.CartService;
import com.ecommerce.data.services.DbOrderService;
import com.ecommerce.data.services.DbPartnerService;
import com.ecommerce.data.services.ProductService;
import com.ecommerce.data.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderService {

    private final CartService cartService;
    private final ProductService productService;
    private final DbOrderService dbOrderService;
    private final UserService userService;

    private final String clientSecrect;
    private final String clientId;
    private final String pos;
    private final String tokenUrl;
    private final String orderUrl;
    private final String language;
    private final String currency;

    private RestTemplate restTemplate;

    @Autowired
    public OrderService(CartService cartService, ProductService productService, @Value("${payu.client-id}") String clientId, @Value("${payu.client-secret}") String clientSecrect, @Value("${payu.pos-id}") String pos, @Value("${payu.token-url}") String tokenUrl, @Value("${payu.order-url}") String orderUrl,
            @Value("${payu.language}") String language, @Value("${payu.currency}") String currency, DbOrderService dbOrderService, UserService userService){
        this.cartService = cartService;
        this.productService = productService;
        this.clientId = clientId;
        this.clientSecrect = clientSecrect;
        this.tokenUrl = tokenUrl;
        this.pos = pos;
        this.orderUrl = orderUrl;
        this.language = language;
        this.currency = currency;
        this.dbOrderService = dbOrderService;
        this.userService = userService;

        this.restTemplate = new RestTemplate();
    }

    public Cart addToCart(String productId, String sessionId){
        Cart cart = cartService.getCartBySessionId(sessionId);

        if(cart == null){
            cart = new Cart();
            cart.setSessionId(sessionId);
        }

        Product product = productService.getProductById(Long.parseLong(productId));
        if(product != null) {
            cart.getProducts().add(product);
        }

        cartService.save(cart);

        return cart;
    }

    public Cart getCart(String sessionId){
        return cartService.getCartBySessionId(sessionId);
    }

    public Cart deleteFromCart(String sessionId, String productId){
        Cart cart = cartService.getCartBySessionId(sessionId);

        if(cart != null){
            cart.getProducts().removeIf(f -> f.getId() == Long.parseLong(productId));
            cartService.save(cart);
        }

        return cart;
    }

    public String getPayUToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","client_credentials");
        map.add("client_id",clientId);
        map.add("client_secret",clientSecrect);

        HttpEntity entity = new HttpEntity(map, headers);

        URI uri = getUri(tokenUrl, null);
        PayUToken res = restTemplate.postForObject(uri, entity, PayUToken.class);
        return res.getAccessToken();
    }

    public String createOrder(OrderDetails orderDetails, String refLink){
        User partner = null;
        if(!StringUtils.isEmpty(refLink)){
            partner = userService.findPartnerByRefLink(refLink);
        }


        String accessToken = getPayUToken();

        Order order = addOrderToDataBase(orderDetails, partner);

        orderDetails.setContinueUrl("http://localhost:8080/notify-order");
        orderDetails.setContinueUrl("http://localhost:8080/order-confirm");
        orderDetails.setMerchantPosId(pos);
        orderDetails.getBuyer().setLanguage(language);
        orderDetails.setCurrencyCode(currency);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity entity = new HttpEntity(orderDetails, headers);
        URI uri = getUri(orderUrl, null);
        OrderResponse res = restTemplate.postForObject(uri, entity, OrderResponse.class);

        return res.getRedirectUri();
    }


    private static URI getUri(String serviceUrl, Map<String, String> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl);

        if(queryParams != null) {
            for (String param : queryParams.keySet()) {
                builder.queryParam(param, queryParams.get(param));
            }
        }

        return builder.path("").build().toUri();
    }

    private Order addOrderToDataBase(OrderDetails orderDetails, User partner){
        Order order = new Order();
        order.setFirstName(orderDetails.getBuyer().getFirstName());
        order.setLastName(orderDetails.getBuyer().getLastName());
        order.setEmail(orderDetails.getBuyer().getEmail());
        order.setAddress(orderDetails.getAddress());
        order.setCity(orderDetails.getCity());
        order.setZipCode(orderDetails.getPostalCode());

        Cart cart = cartService.getCartBySessionId(orderDetails.getSessionId());
        if(cart != null){
            order.setCartId(cart.getId());
        }

        order.setSum(new BigDecimal(orderDetails.getTotalAmount()));
        order.setOrderStatus(OrderStatus.PENDING);

        if(partner != null){
            order.setPartner(partner);
            BigDecimal pointsToAdd = !order.getSum().equals(BigDecimal.ZERO) ? order.getSum().divide(BigDecimal.valueOf(100).setScale(0, RoundingMode.CEILING)) : BigDecimal.ZERO;
            partner.setPoints(partner.getPoints().add(pointsToAdd));
        }


        dbOrderService.save(order);


        orderDetails.setSessionId(null);
        orderDetails.setAddress(null);
        orderDetails.setCity(null);
        orderDetails.setPostalCode(null);

        return order;

    }


}
