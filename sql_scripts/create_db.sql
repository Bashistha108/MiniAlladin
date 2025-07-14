DROP DATABASE IF EXISTS alladin;

CREATE DATABASE alladin;

CREATE USER IF NOT EXISTS 'alladin'@'localhost' IDENTIFIED BY 'alladin';

GRANT ALL PRIVILEGES ON alladin.* TO 'alladin'@'localhost';

FLUSH PRIVILEGES;
