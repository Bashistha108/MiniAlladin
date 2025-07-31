package com.mini.alladin.serviceImpl;

import com.mini.alladin.entity.Stock;
import com.mini.alladin.entity.Trade;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.TradeRepository;
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

    @Autowired
    public TradeServiceImplementation(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
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
    public Trade closeTrade(int tradeId, BigDecimal closePrice) {
        Trade trade = getTradeById(tradeId);

        if (!trade.isOpen()) {
            throw new RuntimeException("Trade already closed");
        }

        trade.setClosePrice(closePrice);
        trade.setCloseTimestamp(LocalDateTime.now());
        trade.setOpen(false);


        BigDecimal pnl;
        if (trade.getTradeType().equalsIgnoreCase("LONG")) {
            pnl = closePrice.subtract(trade.getOpenPrice()).multiply(trade.getQuantity());
        } else {
            pnl = trade.getOpenPrice().subtract(closePrice).multiply(trade.getQuantity());
        }

        trade.setProfitLoss(pnl);
        return tradeRepository.save(trade);
    }
}
