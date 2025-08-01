package com.mini.alladin.serviceImpl;

import com.mini.alladin.entity.Stock;
import com.mini.alladin.entity.Trade;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.TradeRepository;
import com.mini.alladin.repository.UserRepository;
import com.mini.alladin.service.StockPriceService;
import com.mini.alladin.service.TradeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeServiceImplementation implements TradeService {

    private final TradeRepository tradeRepository;
    private final StockPriceService stockPriceService;
    private final UserRepository userRepository;

    @Autowired
    public TradeServiceImplementation(TradeRepository tradeRepository,  StockPriceService stockPriceService, UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.stockPriceService = stockPriceService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Trade createTrade(Trade trade) {
        trade.setOpenTimestamp(LocalDateTime.now());
        trade.setOpen(true);
        return tradeRepository.save(trade);
    }

    @Override
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    @Override
    public List<Trade> getTradesByUser(User user) {
        return tradeRepository.findByUser(user);
    }

    @Override
    public List<Trade> getTradesByUserId(int userId) {
        return tradeRepository.findByUserUserId(userId);
    }

    @Override
    public List<Trade> getOpenTradesByUser(User user) {
        return tradeRepository.findByUserAndIsOpenTrue(user);
    }

    @Override
    public List<Trade> getOpenTradesByUserAndStock(User user, Stock stock) {
        return tradeRepository.findByUserAndStockAndIsOpenTrue(user, stock);
    }

    @Override
    public Trade getTradeById(int id) {
        return tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
    }

    @Override
    @Transactional
    public Trade closeTrade(int tradeId) {
        // Fetch trade from db
        Trade trade = getTradeById(tradeId);

        // Check if already closed
        if (!trade.isOpen()) {
            throw new RuntimeException("Trade already closed");
        }

        // Get live close Price using stockId via stockPriceService
        BigDecimal closePrice = (BigDecimal.valueOf(stockPriceService.getStockPriceByStockId(trade.getStock().getStockId())));

        // Set Closing Fields
        trade.setClosePrice(closePrice);
        trade.setCloseTimestamp(LocalDateTime.now());
        trade.setOpen(false);

        // Calculate P/L
        BigDecimal profitLoss;
        if (trade.getTradeType().equalsIgnoreCase("LONG")) {
            profitLoss = closePrice.subtract(trade.getOpenPrice()).multiply(trade.getQuantity());
        } else {
            profitLoss = trade.getOpenPrice().subtract(closePrice).multiply(trade.getQuantity());
        }

        trade.setProfitLoss(profitLoss);
        return tradeRepository.save(trade);
    }

    @Override
    @Transactional
    public Trade buyStock(User user, Stock stock, BigDecimal quantity, String direction) {
        BigDecimal currentPrice = BigDecimal.valueOf(
                stockPriceService.getStockPriceByStockId(stock.getStockId())
        );

        BigDecimal totalCost = currentPrice.multiply(quantity);

        // Check if user has enough balance
        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Not enough balance to buy this stock.");
        }

        // Deduct balance
        user.setBalance(user.getBalance().subtract(totalCost));
        userRepository.save(user);

        // Create and save trade
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setQuantity(quantity);
        trade.setOpenPrice(currentPrice);
        trade.setTotalInvested(totalCost);
        trade.setTradeType(direction); // LONG / SHORT
        trade.setOpen(true);
        trade.setOpenTimestamp(LocalDateTime.now());

        return tradeRepository.save(trade);
    }


}


