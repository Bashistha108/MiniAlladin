package com.mini.alladin.service;

import com.mini.alladin.dto.OpenTradeDetailDTO;
import com.mini.alladin.dto.PortfolioStockDTO;
import com.mini.alladin.entity.User;

import java.util.List;

public interface PortfolioService {
    List<PortfolioStockDTO> getPortfolioSummary(User user);
    List<OpenTradeDetailDTO> getOpenTradesForStock(User user, int stockId);
}
