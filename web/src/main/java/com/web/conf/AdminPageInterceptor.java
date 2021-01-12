package com.web.conf;

import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.entities.Product;
import com.web.services.ExchangeUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminPageInterceptor implements HandlerInterceptor {

    @Value("${app.service.login}")
    private String loginUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(request.getRequestURL().toString().contains("/admin")){
            Cookie[] cookies = request.getCookies();
            List<Cookie> cookie = Arrays.stream(cookies).filter(f -> f.getName().equalsIgnoreCase("token")).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(cookie)){
                return false;
            }else{
                String token = cookie.get(0).getValue();
                if(StringUtils.isEmpty(token)){
                    return false;
                }

               try {
                   UserDto userDto = ExchangeUtils.exchangeData(loginUrl,
                           "me",
                           HttpMethod.GET,
                           new ParameterizedTypeReference<UserDto>() {},
                           restTemplate,
                           null,
                           token);

                   return userDto != null && userDto.getLogin() != null;
               }catch (Exception e){
                   return false;
               }

            }
        }else{
            return true;
        }
    }
}