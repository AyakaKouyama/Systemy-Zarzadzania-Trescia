package com.web.controllers;


import com.ecommerce.data.entities.User;
import com.ecommerce.data.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public abstract class BasicController {

    @Autowired
    private UserService userService;

    @ModelAttribute("currentUser")
    protected User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return null;

        String username = authentication.getName();
        if (username == null || username.length() < 1)
            return null;

        User user = userService.findUserByLogin(username);
        if (user == null)
            return null;

        return user;
    }
}
