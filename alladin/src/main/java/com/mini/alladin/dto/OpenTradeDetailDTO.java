package com.mini.alladin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenTradeDetailDTO {
    private int tradeId;
    private String stockSymbol;
    private BigDecimal invested;
    private BigDecimal openPrice;
    private BigDecimal quantity;
    private String direction;
    private BigDecimal profitLoss;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;
    private LocalDateTime openTimestamp;
}
