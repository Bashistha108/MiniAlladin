package com.mini.alladin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioStockDTO {
    private int stockId;
    private String stockSymbol;
    private String stockName;
    private BigDecimal totalInvested;
    private BigDecimal averageOpenPrice;
    private BigDecimal quantity;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;
    private BigDecimal profitLoss;
}
