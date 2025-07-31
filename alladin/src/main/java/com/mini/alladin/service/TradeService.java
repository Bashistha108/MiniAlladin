package com.mini.alladin.service;

import com.mini.alladin.entity.Stock;
import com.mini.alladin.entity.Trade;
import com.mini.alladin.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface TradeService {
    Trade createTrade(Trade trade);
    List<Trade> getAllTrades();
    List<Trade> getTradesByUser(User user);
    List<Trade> getTradesByUserId(int userId);
    List<Trade> getOpenTradesByUser(User user);
    List<Trade> getOpenTradesByUserAndStock(User user, Stock stock);
    Trade getTradeById(int id);
    Trade closeTrade(int tradeId, BigDecimal closePrice);
}
