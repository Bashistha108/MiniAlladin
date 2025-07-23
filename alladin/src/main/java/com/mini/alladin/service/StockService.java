package com.mini.alladin.service;

import com.mini.alladin.dto.StockCreateDTO;
import com.mini.alladin.dto.StockDTO;

import java.util.List;

public interface StockService {
    StockDTO createStock(StockCreateDTO stockCreateDTO);
    List<StockDTO> getAllStocks();
    StockDTO getStockById(int stockId);
    StockDTO getStockBySymbol(String symbol);
    StockDTO updateStockByStockId(int stockId, StockCreateDTO stockCreateDTO);
    void deleteStockById(int stockId);
    void deleteStockBySymbol(String symbol);
}
