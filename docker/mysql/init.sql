-- Create databases for each service
CREATE DATABASE IF NOT EXISTS logistics_core_db;
CREATE DATABASE IF NOT EXISTS logistics_b2b_db;
CREATE DATABASE IF NOT EXISTS logistics_b2c_db;
CREATE DATABASE IF NOT EXISTS logistics_config_db;

-- Create user with privileges
CREATE USER IF NOT EXISTS 'logistics_user'@'%' IDENTIFIED BY 'logistics_pass';
GRANT ALL PRIVILEGES ON logistics_core_db.* TO 'logistics_user'@'%';
GRANT ALL PRIVILEGES ON logistics_b2b_db.* TO 'logistics_user'@'%';
GRANT ALL PRIVILEGES ON logistics_b2c_db.* TO 'logistics_user'@'%';
GRANT ALL PRIVILEGES ON logistics_config_db.* TO 'logistics_user'@'%';
GRANT CREATE, ALTER, DROP, REFERENCES ON *.* TO 'logistics_user'@'%';
FLUSH PRIVILEGES;

-- Core database tables
USE logistics_core_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'USER') DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
);

CREATE TABLE IF NOT EXISTS shipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    status ENUM('PENDING', 'PROCESSING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    weight DECIMAL(10,2),
    dimensions VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- B2B database tables
USE logistics_b2b_db;

CREATE TABLE IF NOT EXISTS business_partners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(50) UNIQUE,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- B2C database tables
USE logistics_b2c_db;

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);