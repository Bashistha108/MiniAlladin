package com.mini.alladin.controller;

import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.entity.User;
import com.mini.alladin.service.PortfolioService;
import com.mini.alladin.service.TradeService;
import com.mini.alladin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trader/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;
    private final TradeService tradeService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService, UserService userService,  TradeService tradeService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.tradeService = tradeService;
    }

    @GetMapping
    public String showPortfolio(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        model.addAttribute("portfolioList", portfolioService.getPortfolioSummary(user));
        return "portfolio-overview";
    }

    @GetMapping("/{stockId}")
    public String showPortfolioDetails(@PathVariable int stockId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        model.addAttribute("tradeList", portfolioService.getOpenTradesForStock(user, stockId));
        return "portfolio-details";
    }

    @PostMapping("/close/{tradeId}")
    public String closeTrade(@PathVariable int tradeId) {
        tradeService.closeTrade(tradeId);
        return "redirect:/trader/portfolio";
    }
}
