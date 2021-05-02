package com.web.controller;

import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.exceptions.ApiException;
import com.ecommerce.data.services.UserService;
import com.web.services.ExchangeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/login")
public class LoginController extends BasicController{

    private final String loginUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public LoginController(@Value("${app.service.login}") String loginUrl, RestTemplate restTemplate){
        this.loginUrl = loginUrl;
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "")
    public String loginPage(){
        return "login";
    }

    @RequestMapping(value = "/submit")
    public String submitLogin(ModelMap map, @ModelAttribute UserDto userDto, HttpServletResponse httpServletResponse) {
        if(userDto != null && userDto.getLogin() != null) {
            try {

                ResponseEntity<UserDto> response = restTemplate.postForEntity(loginUrl, userDto, UserDto.class);
                HttpHeaders headers = response.getHeaders();

                Cookie cookie = new Cookie("token", headers.get("Authorization").get(0));
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                //cookie.setSecure(true); to enable on live env
                httpServletResponse.addCookie(cookie);

            }catch(ApiException exc){
                map.put("error", "Nieprawidłowy login lub hasło.");
                return "login";
            }

            return "admin-main";
        }
        return "login";
    }

}

