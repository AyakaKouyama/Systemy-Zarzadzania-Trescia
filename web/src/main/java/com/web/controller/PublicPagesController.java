package com.web.controller;

import com.ecommerce.data.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PublicPagesController extends BasicController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}
