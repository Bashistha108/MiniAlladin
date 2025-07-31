package com.mini.alladin.controller;

import com.mini.alladin.service.LivePriceSchedulerService;
import com.mini.alladin.service.StockPriceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {

    private final StockPriceService stockPriceService;
    private final LivePriceSchedulerService  livePriceSchedulerService;

    public StockPriceController(StockPriceService stockPriceService,  LivePriceSchedulerService livePriceSchedulerService) {
        this.stockPriceService = stockPriceService;
        this.livePriceSchedulerService = livePriceSchedulerService;
    }

    @GetMapping("/stock-price-id/{id}")
    public double getStockPriceWithId(@PathVariable int id) {
        return stockPriceService.getStockPriceByStockId(id);
    }

    @GetMapping("/stock-price-symbol/{symbol}")
    public double getStockPriceWithSymbol(@PathVariable String symbol) {
        return stockPriceService.getStockPriceBySymbol(symbol);
    }

    // Send symbol for @Scheduler
    @PostMapping("/set-symbol")
    public void setCurrentSymbol(@RequestParam String symbol) {
        livePriceSchedulerService.setCurrentSymbol(symbol);
        System.out.println(" Tracking live price for: " + symbol+" FROM StockPriceController");
    }
}
