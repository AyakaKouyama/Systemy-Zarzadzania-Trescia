package com.web.controller;


import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.services.UserService;
import com.web.services.ExchangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public abstract class BasicController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.service.login}")
    String loginUrl;

    protected UserDto getCurrentUser(String token) {
        return ExchangeUtils.exchangeData(loginUrl, "/me", HttpMethod.GET, new ParameterizedTypeReference<UserDto>() {}, restTemplate, null, token);
    }

}
