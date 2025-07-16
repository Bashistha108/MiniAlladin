package com.mini.alladin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String home() {
        return "login"; // serves login.html
    }

    @GetMapping("/token")
    public String tokenPage() {
        return "token"; // serves token.html
    }
}
