package com.mini.alladin.serviceImpl;

import com.mini.alladin.api.FinnhubApiClient;
import com.mini.alladin.entity.Stock;
import com.mini.alladin.repository.StockRepository;
import com.mini.alladin.service.LivePriceSchedulerService;
import com.mini.alladin.websocket.StockPriceWebSocketPublisher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Live Price Scheduler
 * This service runs automatically every 10 seconds.
 * For each tracked stock (e.g. AAPL), it:
 *    1. Fetches the latest price from the API
 *    2. Updates it in the database
 *    3. Broadcasts it via WebSocket
 */
@Slf4j
@Service
public class LivePriceSchedulerServiceImplementation implements LivePriceSchedulerService {

    private final StockRepository stockRepository;
    private final FinnhubApiClient finnhubApiClient;
    private final StockPriceWebSocketPublisher  stockPriceWebSocketPublisher;

    private String symbol = null; // Setting dynamically

    @Autowired
    public LivePriceSchedulerServiceImplementation(StockRepository stockRepository,
                                                   FinnhubApiClient finnhubApiClient,
                                                   StockPriceWebSocketPublisher stockPriceWebSocketPublisher){
        this.stockRepository = stockRepository;
        this.finnhubApiClient = finnhubApiClient;
        this.stockPriceWebSocketPublisher = stockPriceWebSocketPublisher;
    }

    /**
     * This method runs every 10 seconds and updates 1 stock price.
     * You can expand this to loop over multiple symbols if needed.
     * We didn't passed Symbol as parameter because @Scheduled functions does not take parameters
     */
    @Scheduled(fixedRate = 3000)
    @Transactional
    public void updateStockPriceForWebSocket() {

        if(symbol == null){
            System.out.println("Symbol is null: "+ " in LivePriceSchedulerServiceImplementation");
            return;
        }

        System.out.println("Symbol fetched from frontend: "+symbol+" in LivePriceSchedulerServiceImplementation");

        // get current live price
        double price = finnhubApiClient.getLivePrice(symbol);
        System.out.println("LIVE PRICE FOR "+symbol+" "+price+" FROM LivePriceSchedulerServiceImplementaiton");

        // update the price in repository
        Stock stock = stockRepository.findBySymbol(symbol).orElseThrow(() ->  new RuntimeException("stock not found"));
        stock.setCurrentPrice(BigDecimal.valueOf(price));
        stockRepository.save(stock);

        // push updated price to subscribed clients
        stockPriceWebSocketPublisher.sendLivePriceToFrondEndFromWebsocket(symbol, price);

    }

    @Override
    public void setCurrentSymbol(String symbol) {
        this.symbol = symbol;
    }
}
