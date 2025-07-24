package com.mini.alladin.serviceImpl;

import com.mini.alladin.api.FinnhubApiClient;
import com.mini.alladin.entity.Stock;
import com.mini.alladin.repository.StockRepository;
import com.mini.alladin.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StockPriceServiceImplementation implements StockPriceService {

    private final StockRepository stockRepository;
    private final FinnhubApiClient finnhubApiClient;
    @Autowired
    public StockPriceServiceImplementation(StockRepository stockRepository, FinnhubApiClient finnhubApiClient) {
        this.stockRepository = stockRepository;
        this.finnhubApiClient = finnhubApiClient;
    }

    @Override
    public double getStockPriceBySymbol(String symbol) {
        return finnhubApiClient.getLivePrice(symbol);
    }

    @Override
    public double getStockPriceByStockId(int stockId) {
        Optional<Stock> stock = stockRepository.findByStockId(stockId);
        if(stock.isPresent()){
            String stockSymbol = stock.get().getSymbol();
            return finnhubApiClient.getLivePrice(stockSymbol);
        }
        else{
            throw new RuntimeException("stock not found");
        }
    }
}
