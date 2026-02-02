-- Create additional databases for different services
CREATE DATABASE logistics_core_pg;
CREATE DATABASE logistics_b2b_pg;
CREATE DATABASE logistics_b2c_pg;
CREATE DATABASE logistics_analytics_pg;
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE fleet_db;
CREATE DATABASE order_db;
CREATE DATABASE dispatch_db;

-- Create user with privileges
\c logistics_core_pg;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create core tables
CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB
);

CREATE TABLE IF NOT EXISTS shipments (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    sender_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    weight DECIMAL(10,2),
    dimensions VARCHAR(50),
    estimated_delivery DATE,
    actual_delivery TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_shipments_tracking ON shipments(tracking_number);
CREATE INDEX idx_shipments_status ON shipments(status);
CREATE INDEX idx_shipments_created ON shipments(created_at);

-- Create B2B database tables
\c logistics_b2b_pg;

CREATE TABLE IF NOT EXISTS business_partners (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    tax_id VARCHAR(50) UNIQUE,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address JSONB,
    contract_details JSONB,
    credit_limit DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create B2C database tables
\c logistics_b2c_pg;

CREATE TABLE IF NOT EXISTS customers (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address JSONB,
    preferences JSONB,
    loyalty_points INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create analytics database
\c logistics_analytics_pg;

CREATE TABLE IF NOT EXISTS shipment_analytics (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    shipment_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    location VARCHAR(100),
    status VARCHAR(50),
    metadata JSONB,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create user for application access
\c postgres;
CREATE USER logistics_app WITH PASSWORD 'app_password';
GRANT CONNECT ON DATABASE logistics_core_pg TO logistics_app;
GRANT CONNECT ON DATABASE logistics_b2b_pg TO logistics_app;
GRANT CONNECT ON DATABASE logistics_b2c_pg TO logistics_app;
GRANT CONNECT ON DATABASE logistics_analytics_pg TO logistics_app;
GRANT CONNECT ON DATABASE auth_db TO logistics_app;
GRANT CONNECT ON DATABASE user_db TO logistics_app;
GRANT CONNECT ON DATABASE fleet_db TO logistics_app;
GRANT CONNECT ON DATABASE order_db TO logistics_app;
GRANT CONNECT ON DATABASE dispatch_db TO logistics_app;

-- Grant privileges
GRANT USAGE ON SCHEMA public TO logistics_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO logistics_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO logistics_app;