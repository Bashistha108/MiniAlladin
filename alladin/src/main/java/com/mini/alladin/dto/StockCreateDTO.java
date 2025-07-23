package com.mini.alladin.dto;

import lombok.*;


/**
 * This is the request DTO used when creating/updating a stock
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCreateDTO {
    private String symbol;
    private String name;
    private String exchange;
    private String currency;
    private String sector;
    private double currentPrice;
}
