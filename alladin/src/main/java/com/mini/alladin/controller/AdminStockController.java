package com.mini.alladin.controller;

import com.mini.alladin.dto.StockCreateDTO;
import com.mini.alladin.dto.StockDTO;
import com.mini.alladin.service.StockPriceService;
import com.mini.alladin.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/manage-stocks")
public class AdminStockController {

    private final StockService stockService;
    private final StockPriceService stockPriceService;

    public AdminStockController(StockService stockService, StockPriceService stockPriceService) {
        this.stockService = stockService;
        this.stockPriceService = stockPriceService;
    }

    @GetMapping
    public String viewStocks(Model model) {
        List<StockDTO> stocks = stockService.getAllStocks();

        for(StockDTO stockDTO : stocks){
            String stockSymbol =  stockDTO.getSymbol();
            double liveStockPrice = stockPriceService.getStockPriceBySymbol(stockSymbol);

            // Convert DTO to CreateDTO to update the stock in db
            StockCreateDTO stockCreateDTO = new StockCreateDTO();
            stockCreateDTO.setSymbol(stockDTO.getSymbol());
            stockCreateDTO.setCurrency(stockDTO.getCurrency());
            stockCreateDTO.setExchange(stockDTO.getExchange());
            stockCreateDTO.setSector(stockDTO.getSector());
            stockCreateDTO.setCurrentPrice(liveStockPrice);
            stockCreateDTO.setName(stockDTO.getName());
            // updating in db
            stockService.updateStockByStockId(stockDTO.getStockId(), stockCreateDTO);
        }

        model.addAttribute("stocks", stocks);
        return "admin-manage-stocks";
    }

    @PostMapping("/create")
    public String createStock(@ModelAttribute StockCreateDTO dto) {
        stockService.createStock(dto);
        return "redirect:/admin/manage-stocks";
    }

    @PostMapping("/delete/{id}")
    public String deleteStock(@PathVariable int id) {
        stockService.deleteStockById(id);
        return "redirect:/admin/manage-stocks";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        StockDTO stock = stockService.getStockById(id);
        model.addAttribute("stock", stock);
        return "admin-update-stock";
    }

    @PostMapping("/update/{id}")
    public String updateStock(@PathVariable int id, @ModelAttribute StockDTO stockDTO) {
        StockCreateDTO dto = new StockCreateDTO(
                stockDTO.getSymbol(),
                stockDTO.getName(),
                stockDTO.getExchange(),
                stockDTO.getCurrency(),
                stockDTO.getSector(),
                stockDTO.getCurrentPrice()
        );
        stockService.updateStockByStockId(id, dto);
        return "redirect:/admin/manage-stocks?updated=true";
    }



}
