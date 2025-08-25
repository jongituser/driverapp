-- Customer Management Migration
-- V5__customer_management.sql

-- Create customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(254) UNIQUE,
    preferred_payment VARCHAR(50),
    default_address_id BIGINT,
    region VARCHAR(100),
    delivery_preferences TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customers_user_id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_customers_default_address_id FOREIGN KEY (default_address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

-- Create addresses table
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    landmark VARCHAR(100),
    additional_instructions VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_addresses_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Add customer_id to deliveries table
ALTER TABLE delivery ADD COLUMN customer_id BIGINT;
ALTER TABLE delivery ADD CONSTRAINT fk_delivery_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL;

-- Create indexes for customers table
CREATE INDEX ix_customer_user_id ON customers(user_id);
CREATE INDEX ix_customer_phone ON customers(phone);
CREATE INDEX ix_customer_email ON customers(email);
CREATE INDEX ix_customer_full_name ON customers(full_name);
CREATE INDEX ix_customer_region ON customers(region);
CREATE INDEX ix_customer_active ON customers(active);
CREATE INDEX ix_customer_verified ON customers(verified);

-- Create indexes for addresses table
CREATE INDEX ix_address_customer_id ON addresses(customer_id);
CREATE INDEX ix_address_is_default ON addresses(is_default);
CREATE INDEX ix_address_region ON addresses(region);
CREATE INDEX ix_address_city ON addresses(city);
CREATE INDEX ix_address_active ON addresses(active);

-- Create indexes for delivery table
CREATE INDEX ix_delivery_customer_id ON delivery(customer_id);

-- Add comments
COMMENT ON TABLE customers IS 'Customer information and preferences';
COMMENT ON COLUMN customers.user_id IS 'Reference to user account';
COMMENT ON COLUMN customers.full_name IS 'Customer full name';
COMMENT ON COLUMN customers.phone IS 'Customer phone number (unique)';
COMMENT ON COLUMN customers.email IS 'Customer email address (unique)';
COMMENT ON COLUMN customers.preferred_payment IS 'Preferred payment method (CASH, CARD, MOBILE_MONEY, BANK_TRANSFER)';
COMMENT ON COLUMN customers.default_address_id IS 'Default delivery address';
COMMENT ON COLUMN customers.region IS 'Customer region/location';
COMMENT ON COLUMN customers.delivery_preferences IS 'JSON string for delivery preferences';
COMMENT ON COLUMN customers.active IS 'Whether customer account is active';
COMMENT ON COLUMN customers.verified IS 'Whether customer is verified';

COMMENT ON TABLE addresses IS 'Customer delivery addresses';
COMMENT ON COLUMN addresses.customer_id IS 'Reference to customer';
COMMENT ON COLUMN addresses.address_line_1 IS 'Primary address line';
COMMENT ON COLUMN addresses.address_line_2 IS 'Secondary address line (optional)';
COMMENT ON COLUMN addresses.city IS 'City name';
COMMENT ON COLUMN addresses.region IS 'Region/state name';
COMMENT ON COLUMN addresses.postal_code IS 'Postal/ZIP code';
COMMENT ON COLUMN addresses.landmark IS 'Nearby landmark for easy identification';
COMMENT ON COLUMN addresses.additional_instructions IS 'Special delivery instructions';
COMMENT ON COLUMN addresses.is_default IS 'Whether this is the default address';
COMMENT ON COLUMN addresses.active IS 'Whether address is active';

-- Insert sample data for testing
INSERT INTO customers (user_id, full_name, phone, email, preferred_payment, region, delivery_preferences, active, verified) VALUES
(1, 'Abebe Kebede', '+251911234567', 'abebe.kebede@email.com', 'CASH', 'Addis Ababa', '{"preferredTime": "morning", "instructions": "Call before delivery"}', true, true),
(2, 'Kebede Alemu', '+251922345678', 'kebede.alemu@email.com', 'MOBILE_MONEY', 'Addis Ababa', '{"preferredTime": "afternoon", "instructions": "Leave at gate"}', true, false),
(3, 'Alemayehu Tadesse', '+251933456789', 'alemayehu.tadesse@email.com', 'CARD', 'Dire Dawa', '{"preferredTime": "evening", "instructions": "Ring doorbell twice"}', true, true),
(4, 'Tadesse Haile', '+251944567890', 'tadesse.haile@email.com', 'BANK_TRANSFER', 'Bahir Dar', '{"preferredTime": "morning", "instructions": "Call when arriving"}', true, false),
(5, 'Haile Selassie', '+251955678901', 'haile.selassie@email.com', 'CASH', 'Mekelle', '{"preferredTime": "afternoon", "instructions": "Text when 10 minutes away"}', true, true);

-- Insert sample addresses
INSERT INTO addresses (customer_id, address_line_1, address_line_2, city, region, postal_code, landmark, additional_instructions, is_default, active) VALUES
(1, 'Bole Road', 'Building 123, Apartment 4A', 'Addis Ababa', 'Addis Ababa', '1000', 'Near Bole Airport', 'Call before delivery', true, true),
(1, 'Kazanchis', 'Building 456, Floor 2', 'Addis Ababa', 'Addis Ababa', '1001', 'Near Commercial Bank', 'Security guard will assist', false, true),
(2, 'Piazza', 'Building 789, Unit 12', 'Addis Ababa', 'Addis Ababa', '1002', 'Near Post Office', 'Leave at gate if no answer', true, true),
(3, 'Dire Dawa Main Street', 'Building 321, Apartment 7B', 'Dire Dawa', 'Dire Dawa', '3000', 'Near Train Station', 'Ring doorbell twice', true, true),
(4, 'Lake Tana Road', 'Building 654, Floor 3', 'Bahir Dar', 'Amhara', '6000', 'Near Blue Nile Hotel', 'Call when arriving', true, true),
(5, 'Mekelle Central', 'Building 987, Unit 5', 'Mekelle', 'Tigray', '7000', 'Near University', 'Text when 10 minutes away', true, true);

-- Update the default_address_id references
UPDATE customers SET default_address_id = 1 WHERE id = 1;
UPDATE customers SET default_address_id = 3 WHERE id = 2;
UPDATE customers SET default_address_id = 4 WHERE id = 3;
UPDATE customers SET default_address_id = 5 WHERE id = 4;
UPDATE customers SET default_address_id = 6 WHERE id = 5;
