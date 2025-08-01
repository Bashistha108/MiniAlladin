package com.mini.alladin.controller;

import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;

    @Autowired
    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/trader/trader-dashboard")
    public String traderDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "trader-dashboard";
    }
    @GetMapping("/admin/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}
