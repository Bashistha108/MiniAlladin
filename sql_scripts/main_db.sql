USE alladin;


-- Roles
CREATE TABLE roles (
                       role_id INT PRIMARY KEY,
                       role_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (role_id, role_name) VALUES
                                           (1, 'ADMIN'),
                                           (2, 'TRADER');


-- Users
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255),
                       role_id INT,
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       profile_picture VARCHAR(255),
                       FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

INSERT INTO users (first_name, last_name, email, password, role_id)
VALUES ('Test', 'Admin', 'admin@admin.com', '1234', 1),
       ('Test', 'Trader', 'trader@trader.com', '1234', 2);

-- Stocks
CREATE TABLE stocks (
                        stock_id INT AUTO_INCREMENT PRIMARY KEY,
                        symbol VARCHAR(10) UNIQUE NOT NULL,
                        name VARCHAR(100),
                        exchange VARCHAR(50),
                        currency VARCHAR(10),
                        sector VARCHAR(50),
                        current_price DECIMAL(12,4),
                        price_updated_at TIMESTAMP
);

-- Portfolio Items
CREATE TABLE portfolio_items (
                                 portfolio_item_id INT AUTO_INCREMENT PRIMARY KEY,
                                 user_id INT,
                                 stock_id INT,
                                 quantity DECIMAL(12,4),
                                 average_price DECIMAL(12,4),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES users(user_id),
                                 FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- Trades
CREATE TABLE trades (
                        trade_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT,
                        stock_id INT,
                        trade_type VARCHAR(10), -- e.g. BUY/SELL
                        price DECIMAL(12,4),
                        quantity DECIMAL(12,4),
                        total DECIMAL(12,4),
                        trade_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(user_id),
                        FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- Posts
CREATE TABLE posts (
                       post_id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT,
                       content TEXT,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Audit Logs
CREATE TABLE audit_logs (
                            log_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT,
                            action VARCHAR(100),
                            description TEXT,
                            logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Stock Prices (historical)
CREATE TABLE stock_prices (
                              price_id INT AUTO_INCREMENT PRIMARY KEY,
                              stock_id INT,
                              date TIMESTAMP,
                              open_price DECIMAL(12,4),
                              high_price DECIMAL(12,4),
                              low_price DECIMAL(12,4),
                              close_price DECIMAL(12,4),
                              volume DECIMAL(12,4),
                              FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- Risk Analytics
CREATE TABLE risk_analytics (
                                analytics_id INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT,
                                date TIMESTAMP,
                                portfolio_value DECIMAL(12,4),
                                value_at_risk DECIMAL(12,4),
                                beta DECIMAL(12,4),
                                FOREIGN KEY (user_id) REFERENCES users(user_id)
);
