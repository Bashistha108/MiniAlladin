package com.mini.alladin.repository;

import com.mini.alladin.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
     Optional<Stock> findByStockId(int stockId);
     Optional<Stock> findBySymbol(String symbol);
     boolean existsBySymbol(String symbol);
}
