package com.web.conf;

import com.ecommerce.data.dtos.UserDto;
import com.web.services.ExchangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
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

        if (request.getRequestURL().toString().contains("/admin")) {
            Cookie[] cookies = request.getCookies();
            List<Cookie> cookie = Arrays.stream(cookies)
                    .filter(f -> f.getName().equalsIgnoreCase("token"))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(cookie)) {
                return false;
            } else {
                String token = cookie.get(0).getValue();
                if (StringUtils.isEmpty(token)) {
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
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return true;
        }
    }
}