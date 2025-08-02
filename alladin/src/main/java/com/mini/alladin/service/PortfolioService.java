package com.mini.alladin.service;

import com.mini.alladin.dto.OpenTradeDetailDTO;
import com.mini.alladin.dto.PortfolioStockDTO;
import com.mini.alladin.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PortfolioService {
    List<PortfolioStockDTO> getPortfolioSummary(User user);
    List<OpenTradeDetailDTO> getOpenTradesForStock(User user, int stockId);

    Map<String, BigDecimal> calculatePortfolioMetrics(User user);
}
