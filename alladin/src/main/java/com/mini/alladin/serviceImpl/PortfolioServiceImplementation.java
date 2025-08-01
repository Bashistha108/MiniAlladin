package com.mini.alladin.serviceImpl;

import com.mini.alladin.dto.OpenTradeDetailDTO;
import com.mini.alladin.dto.PortfolioStockDTO;
import com.mini.alladin.entity.Stock;
import com.mini.alladin.entity.Trade;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.StockRepository;
import com.mini.alladin.service.PortfolioService;
import com.mini.alladin.service.StockPriceService;
import com.mini.alladin.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImplementation implements PortfolioService {

    private final TradeService tradeService;
    private final StockPriceService stockPriceService;
    private final StockRepository stockRepository;

    @Autowired
    public PortfolioServiceImplementation(TradeService tradeService,
                                          StockPriceService stockPriceService,
                                          StockRepository stockRepository) {
        this.tradeService = tradeService;
        this.stockPriceService = stockPriceService;
        this.stockRepository = stockRepository;
    }


    @Override
    public List<PortfolioStockDTO> getPortfolioSummary(User user) {
        List<Trade> openTrades = tradeService.getOpenTradesByUser(user);

        // Group by Stock
        Map<Stock, List<Trade>> tradesByStock = openTrades.stream()
                .collect(Collectors.groupingBy(Trade::getStock));

        List<PortfolioStockDTO>  portfolioStockDTOList = new ArrayList<>();

        for(Map.Entry<Stock, List<Trade>> entry : tradesByStock.entrySet()){
            Stock stock = entry.getKey();
            List<Trade> trades = entry.getValue();

            BigDecimal totalInvested = trades.stream()
                    .map(Trade::getTotalInvested)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalQuantity = trades.stream()
                    .map(Trade::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal averageOpenPrice = totalQuantity.compareTo(BigDecimal.ZERO) > 0
                    ? totalInvested.divide(totalQuantity, 2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO;


            BigDecimal currentPrice = BigDecimal.valueOf(stockPriceService.getStockPriceByStockId(stock.getStockId()));

            BigDecimal totalValue = currentPrice.multiply(totalQuantity);

            BigDecimal profitLoss = totalValue.subtract(totalInvested);

            portfolioStockDTOList.add(PortfolioStockDTO.builder()
                    .stockId(stock.getStockId())
                    .stockSymbol(stock.getSymbol())
                    .stockName(stock.getName())
                    .totalInvested(totalInvested)
                    .averageOpenPrice(averageOpenPrice)
                    .quantity(totalQuantity)
                    .currentPrice(currentPrice)
                    .totalValue(totalValue)
                    .profitLoss(profitLoss)
                    .build());
        }

        return portfolioStockDTOList;

    }

    @Override
    public List<OpenTradeDetailDTO> getOpenTradesForStock(User user, int stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        List<Trade> trades = tradeService.getOpenTradesByUserAndStock(user, stock);
        BigDecimal currentPrice = BigDecimal.valueOf(stockPriceService.getStockPriceByStockId(stockId));

        return trades.stream()
                .map(trade -> {
                    BigDecimal pnl;
                    if (trade.getTradeType().equalsIgnoreCase("LONG")) {
                        pnl = currentPrice.subtract(trade.getOpenPrice()).multiply(trade.getQuantity());
                    } else {
                        pnl = trade.getOpenPrice().subtract(currentPrice).multiply(trade.getQuantity());
                    }

                    return OpenTradeDetailDTO.builder()
                            .tradeId(trade.getTradeId())
                            .stockSymbol(stock.getSymbol())
                            .openPrice(trade.getOpenPrice())
                            .quantity(trade.getQuantity())
                            .direction(trade.getTradeType())
                            .profitLoss(pnl)
                            .invested(trade.getTotalInvested())
                            .totalValue(currentPrice.multiply(trade.getQuantity()))
                            .currentPrice(currentPrice)
                            .openTimestamp(trade.getOpenTimestamp())
                            .build();
                }).toList();
    }
}
