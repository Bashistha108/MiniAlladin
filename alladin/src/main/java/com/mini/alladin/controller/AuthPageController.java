package com.mini.alladin.controller;


import com.mini.alladin.dto.UserCreateDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI pages for login / Register
 * */
@Controller
public class AuthPageController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/token")
    public String tokenPage() {
        return "token";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateDTO());
        return "register";
    }


}
