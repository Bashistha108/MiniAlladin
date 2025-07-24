package com.mini.alladin.serviceImpl;

import com.mini.alladin.dto.StockCreateDTO;
import com.mini.alladin.dto.StockDTO;
import com.mini.alladin.entity.Stock;
import com.mini.alladin.repository.StockRepository;
import com.mini.alladin.service.StockPriceService;
import com.mini.alladin.service.StockService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImplementation implements StockService {

    private final StockRepository stockRepository;
    private final StockPriceService stockPriceService;

    @Autowired
    public StockServiceImplementation(StockRepository stockRepository, StockPriceService stockPriceService) {
        this.stockRepository = stockRepository;
        this.stockPriceService = stockPriceService;
    }

    @Transactional
    @Override
    public StockDTO createStock(StockCreateDTO stockCreateDTO) {

        if(stockRepository.existsBySymbol(stockCreateDTO.getSymbol())){
            throw new RuntimeException("Stock already exists with symbol " + stockCreateDTO.getSymbol());
        }

        Stock stock = new Stock();
        stock.setSymbol(stockCreateDTO.getSymbol().toUpperCase());
        stock.setName(stockCreateDTO.getName());
        stock.setExchange(stockCreateDTO.getExchange().toUpperCase());
        stock.setCurrency(stockCreateDTO.getCurrency().toUpperCase());
        stock.setSector(stockCreateDTO.getSector());
        // Saving stock before saving its current price
        stockRepository.save(stock);
        // Set live price, User doesn't give the current price
        double livePriceFromApi = stockPriceService.getStockPriceByStockId(stock.getStockId());
        stock.setCurrentPrice(BigDecimal.valueOf(livePriceFromApi));


        return toDTO(stockRepository.save(stock));
    }

    @Override
    public List<StockDTO> getAllStocks() {
        return stockRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public StockDTO getStockById(int stockId) {
        Stock stock = stockRepository.findByStockId(stockId).orElseThrow(() -> new RuntimeException("Stock not found with id " + stockId));
        return toDTO(stock);
    }

    @Override
    public StockDTO getStockBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol).orElseThrow(()->new RuntimeException("Stock not found with symbol " + symbol));
        return toDTO(stock);
    }

    @Transactional
    @Override
    public StockDTO updateStockByStockId(int stockId, StockCreateDTO stockCreateDTO) {

        Stock stock = stockRepository.findByStockId(stockId).orElseThrow(() -> new RuntimeException("Stock not found with id " + stockId));

        stock.setSymbol(stockCreateDTO.getSymbol().toUpperCase());
        stock.setName(stockCreateDTO.getName());
        stock.setExchange(stockCreateDTO.getExchange().toUpperCase());
        stock.setCurrency(stockCreateDTO.getCurrency().toUpperCase());
        stock.setSector(stockCreateDTO.getSector());
        stock.setCurrentPrice(BigDecimal.valueOf(stockCreateDTO.getCurrentPrice()));

        stockRepository.save(stock);
        return toDTO(stock);
    }


    @Transactional
    @Override
    public void deleteStockById(int stockId) {
        if (!stockRepository.existsById(stockId)) {
            throw new RuntimeException("Stock not found");
        }
        stockRepository.deleteById(stockId);
    }

    @Transactional
    @Override
    public void deleteStockBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol).orElseThrow(() -> new RuntimeException("Stock not found with symbol " + symbol));
        stockRepository.delete(stock);
    }

    private StockDTO toDTO(Stock stock){
        StockDTO stockDTO = new StockDTO();
        stockDTO.setStockId(stock.getStockId());
        stockDTO.setSymbol(stock.getSymbol());
        stockDTO.setName(stock.getName());
        stockDTO.setExchange(stock.getExchange());
        stockDTO.setCurrency(stock.getCurrency());
        stockDTO.setSector(stock.getSector());
        stockDTO.setCurrentPrice(stock.getCurrentPrice().doubleValue());
        stockDTO.setPriceUpdatedAt(stock.getPriceUpdatedAt());

        return stockDTO;
    }
}
