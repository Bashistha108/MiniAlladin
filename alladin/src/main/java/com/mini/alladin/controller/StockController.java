package com.mini.alladin.controller;

import com.mini.alladin.dto.StockCreateDTO;
import com.mini.alladin.dto.StockDTO;
import com.mini.alladin.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * This class only serves for testing through Postman.
 * We won't use it anywhere in the App.
 * */
@RestController
@RequestMapping("/api/stocks")
public class StockController {


    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    @PostMapping("/create")
    public StockDTO create(@RequestBody StockCreateDTO stockCreateDTO){
        return stockService.createStock(stockCreateDTO);
    }

    @GetMapping("/get-by-id/{id}")
    public StockDTO getById(@PathVariable int id){
        return stockService.getStockById(id);
    }

    @GetMapping("/get-by-symbol/{symbol}")
    public StockDTO getBySymbol(@PathVariable String symbol){
        return stockService.getStockBySymbol(symbol);
    }

    @GetMapping("/get-all")
    public List<StockDTO> getAll(){
        return stockService.getAllStocks();
    }

    @PutMapping("/update-by-id/{id}")
    public StockDTO updateById(@PathVariable int id, @RequestBody StockCreateDTO stockCreateDTO){
        return stockService.updateStockByStockId(id, stockCreateDTO);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public void deleteById(@PathVariable int id){
        stockService.deleteStockById(id);
    }




}
