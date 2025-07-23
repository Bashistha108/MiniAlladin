package com.mini.alladin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This is the response DTO shown to client (Admin or Trader)
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private int stockId;
    private String symbol;
    private String name;
    private String exchange;
    private String currency;
    private String sector;
    private double currentPrice;
    private LocalDateTime priceUpdatedAt;
}
