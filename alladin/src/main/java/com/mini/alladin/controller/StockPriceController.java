package com.mini.alladin.controller;

import com.mini.alladin.service.StockPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {

    private StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/stock-price-id/{id}")
    public double getStockPriceWithId(@PathVariable int id) {
        return stockPriceService.getStockPriceByStockId(id);
    }

    @GetMapping("/stock-price-symbol/{symbol}")
    public double getStockPriceWithSymbol(@PathVariable String symbol) {
        return stockPriceService.getStockPriceBySymbol(symbol);
    }
}
