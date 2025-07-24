USE alladin;

DROP TABLE IF EXISTS trades;
DROP TABLE IF EXISTS portfolio_items;

CREATE TABLE trades(
	trade_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    stock_id INT NOT NULL,
    
    trade_type VARCHAR(10) NOT NULL,  -- Long / Short
    open_price DECIMAL(12,4) NOT NULL,     -- Open Price
    quantity DECIMAL(12,4) NOT NULL,
    total_invested DECIMAL(12,4) NOT NULL,        -- open_price * quantity
    
    is_open BOOLEAN DEFAULT TRUE, 
    open_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    close_price DECIMAL(12,4),
    close_timestamp TIMESTAMP,
    profit_loss DECIMAL(12,4),
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
    
);
