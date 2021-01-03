package com.web.controller;

import com.ecommerce.data.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(value = "/login")
public class LoginController extends BasicController{

    @RequestMapping(value = "")
    public String loginPage(@Param("error") String error, ModelMap map) {
        if (error != null) {
            map.put("error", "Invalid login or password");
        } else {
            map.put("error", "");
            log.warn("Invalid credentials.");
        }

        return "login";
    }
}

