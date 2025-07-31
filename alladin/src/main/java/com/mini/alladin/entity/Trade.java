package com.mini.alladin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name="trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tradeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "trade_type", nullable = false)
    private String tradeType; // LONG or SHORT

    @Column(name = "open_price", nullable = false)
    private BigDecimal openPrice;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "total_invested", nullable = false)
    private BigDecimal totalInvested;

    @Column(name = "is_open")
    private boolean isOpen = true;

    @Column(name = "open_timestamp")
    private LocalDateTime openTimestamp;

    @Column(name = "close_price")
    private BigDecimal closePrice;

    @Column(name = "close_timestamp")
    private LocalDateTime closeTimestamp;

    @Column(name = "profit_loss")
    private BigDecimal profitLoss;

}
