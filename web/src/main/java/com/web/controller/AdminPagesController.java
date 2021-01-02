package com.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminPagesController {

    @RequestMapping(value = "/main")
    public String adminMainPage() {
        return "admin-main";
    }

}
