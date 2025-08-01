package com.mini.alladin.controller;

import com.mini.alladin.entity.Stock;
import com.mini.alladin.entity.User;
import com.mini.alladin.service.StockPriceService;
import com.mini.alladin.service.StockService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;




@Controller
public class TradeController {

    private final StockService stockService;
    private final UserService userService;
    private final TradeService tradeService;
    private final StockPriceService stockPriceService;

    @Autowired
    public TradeController(StockService stockService, UserService userService, TradeService tradeService, StockPriceService stockPriceService) {
        this.stockService = stockService;
        this.userService = userService;
        this.tradeService = tradeService;
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/trader/trade/{stockId}")
    public String showTradeForm(@PathVariable int stockId,
                                @RequestParam(value = "action", required = false, defaultValue = "LONG") String action,
                                Model model) {
        Stock stock = stockService.getStockEntityById(stockId);
        double livePrice = stockPriceService.getStockPriceByStockId(stockId);

        model.addAttribute("stock", stock);
        model.addAttribute("direction", action);
        model.addAttribute("symbol", stock.getSymbol());
        model.addAttribute("price", livePrice);
        return "stock-buy-sell";
    }



    @PostMapping("/trader/trade/submit")
    public String submitTrade(@RequestParam int stockId,
                              @RequestParam BigDecimal quantity,
                              @RequestParam String direction,
                              @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println("✅ SUBMIT TRADE: stockId=" + stockId + ", qty=" + quantity + ", direction=" + direction);

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        Stock stock = stockService.getStockEntityById(stockId);

        tradeService.buyStock(user, stock, quantity, direction); // direction can be LONG or SHORT

        return "redirect:/trader/portfolio";
    }

}
