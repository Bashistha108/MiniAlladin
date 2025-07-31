package com.mini.alladin.repository;

import com.mini.alladin.entity.Stock;
import com.mini.alladin.entity.Trade;
import com.mini.alladin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {

    List<Trade> findByUserUserId(int userId);
    List<Trade> findByUser(User user);

    List<Trade> findByUserAndIsOpenTrue(User user);
    List<Trade> findByUserAndStockAndIsOpenTrue(User user, Stock stock);

}
