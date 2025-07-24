package com.mini.alladin.service;

public interface StockPriceService {
    double getStockPriceBySymbol(String symbol);
    double getStockPriceByStockId(int stockId);
}
