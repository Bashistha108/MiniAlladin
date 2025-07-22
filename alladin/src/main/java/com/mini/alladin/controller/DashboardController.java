package com.mini.alladin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/trader/trader-dashboard")
    public String traderDashboard() {
        return "trader-dashboard";
    }
    @GetMapping("/admin/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}
