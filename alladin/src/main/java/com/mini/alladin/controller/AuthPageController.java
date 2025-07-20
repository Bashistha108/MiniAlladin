package com.mini.alladin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String registerPage() {
        return "register";
    }

}
