package com.mini.alladin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int stockId;

    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "exchange", length = 50)
    private String exchange;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "sector", length = 50)
    private String sector;

    @Column(name = "current_price", precision = 12, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "price_updated_at")
    @UpdateTimestamp
    private LocalDateTime priceUpdatedAt;

}
