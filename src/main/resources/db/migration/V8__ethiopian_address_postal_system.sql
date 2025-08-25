-- Ethiopian Address & Postal System Migration
-- GPS-first support with optional Woreda/Kebele/Postal components

-- Create enum types
CREATE TYPE ethiopian_region AS ENUM (
    'ADDIS_ABABA',
    'AFAR',
    'AMHARA',
    'BENISHANGUL_GUMUZ',
    'DIRE_DAWA',
    'GAMBELLA',
    'HARARI',
    'OROMIA',
    'SIDAMA',
    'SOMALI',
    'SOUTHERN_NATIONS',
    'SOUTH_WEST_ETHIOPIA',
    'TIGRAY'
);

-- Create postal_codes table
CREATE TABLE postal_codes (
    id BIGSERIAL PRIMARY KEY,
    region ethiopian_region NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for postal_codes
CREATE INDEX ix_postal_code_region ON postal_codes(region);
CREATE INDEX ix_postal_code_code ON postal_codes(code);
CREATE INDEX ix_postal_code_region_code ON postal_codes(region, code);
CREATE INDEX ix_postal_code_active ON postal_codes(active);

-- Drop existing addresses table if it exists (from previous migrations)
DROP TABLE IF EXISTS addresses CASCADE;

-- Create new addresses table with GPS-first support
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    
    -- GPS Coordinates (Required)
    gps_lat DECIMAL(10,8) NOT NULL CHECK (gps_lat >= -90 AND gps_lat <= 90),
    gps_long DECIMAL(11,8) NOT NULL CHECK (gps_long >= -180 AND gps_long <= 180),
    
    -- Ethiopian Administrative Structure (Optional)
    region ethiopian_region,
    woreda VARCHAR(100),
    kebele VARCHAR(100),
    
    -- Postal Code (Optional)
    postal_code_id BIGINT REFERENCES postal_codes(id),
    
    -- Description (Free text)
    description VARCHAR(500),
    
    -- Relationships
    customer_id BIGINT REFERENCES customers(id),
    partner_id BIGINT REFERENCES partners(id),
    
    -- Metadata
    active BOOLEAN DEFAULT TRUE NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for addresses
CREATE INDEX ix_address_customer_id ON addresses(customer_id);
CREATE INDEX ix_address_partner_id ON addresses(partner_id);
CREATE INDEX ix_address_gps ON addresses(gps_lat, gps_long);
CREATE INDEX ix_address_region ON addresses(region);
CREATE INDEX ix_address_woreda ON addresses(woreda);
CREATE INDEX ix_address_kebele ON addresses(kebele);
CREATE INDEX ix_address_postal_code ON addresses(postal_code_id);
CREATE INDEX ix_address_active ON addresses(active);

-- Create composite indexes for common queries
CREATE INDEX ix_address_region_woreda ON addresses(region, woreda);
CREATE INDEX ix_address_region_woreda_kebele ON addresses(region, woreda, kebele);
CREATE INDEX ix_address_customer_active ON addresses(customer_id, active);
CREATE INDEX ix_address_partner_active ON addresses(partner_id, active);

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_postal_codes_updated_at BEFORE UPDATE ON postal_codes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_addresses_updated_at BEFORE UPDATE ON addresses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample postal codes for major Ethiopian cities
INSERT INTO postal_codes (region, code, description) VALUES
-- Addis Ababa
('ADDIS_ABABA', '1000', 'Addis Ababa Central'),
('ADDIS_ABABA', '1001', 'Addis Ababa North'),
('ADDIS_ABABA', '1002', 'Addis Ababa South'),
('ADDIS_ABABA', '1003', 'Addis Ababa East'),
('ADDIS_ABABA', '1004', 'Addis Ababa West'),

-- Amhara Region
('AMHARA', '2000', 'Bahir Dar'),
('AMHARA', '2001', 'Gondar'),
('AMHARA', '2002', 'Dessie'),
('AMHARA', '2003', 'Debre Markos'),
('AMHARA', '2004', 'Debre Birhan'),

-- Oromia Region
('OROMIA', '3000', 'Adama'),
('OROMIA', '3001', 'Jimma'),
('OROMIA', '3002', 'Nekemte'),
('OROMIA', '3003', 'Bishoftu'),
('OROMIA', '3004', 'Shashamane'),

-- Tigray Region
('TIGRAY', '4000', 'Mekelle'),
('TIGRAY', '4001', 'Adigrat'),
('TIGRAY', '4002', 'Axum'),
('TIGRAY', '4003', 'Shire'),

-- Southern Nations
('SOUTHERN_NATIONS', '5000', 'Hawassa'),
('SOUTHERN_NATIONS', '5001', 'Arba Minch'),
('SOUTHERN_NATIONS', '5002', 'Dilla'),
('SOUTHERN_NATIONS', '5003', 'Hosaena'),

-- Dire Dawa
('DIRE_DAWA', '6000', 'Dire Dawa Central'),
('DIRE_DAWA', '6001', 'Dire Dawa Industrial'),

-- Harari
('HARARI', '7000', 'Harar'),

-- Afar
('AFAR', '8000', 'Semera'),
('AFAR', '8001', 'Asaita'),

-- Somali
('SOMALI', '9000', 'Jijiga'),
('SOMALI', '9001', 'Gode'),

-- Gambella
('GAMBELLA', '10000', 'Gambella'),

-- Benishangul-Gumuz
('BENISHANGUL_GUMUZ', '11000', 'Assosa'),

-- Sidama
('SIDAMA', '12000', 'Hawassa'),

-- South West Ethiopia
('SOUTH_WEST_ETHIOPIA', '13000', 'Bonga');

-- Insert sample addresses for testing
INSERT INTO addresses (gps_lat, gps_long, region, woreda, kebele, description, customer_id) VALUES
-- GPS-only address
(9.0320, 38.7489, NULL, NULL, NULL, 'GPS coordinates only - blue gate house near church', 1),

-- Full Ethiopian address
(9.0320, 38.7489, 'ADDIS_ABABA', 'Kolfe Keranio', 'Kebele 01', 'Full Ethiopian address with GPS', 1),

-- Hybrid address (GPS + Ethiopian)
(8.9806, 38.7578, 'ADDIS_ABABA', 'Bole', 'Kebele 03', 'Hybrid address - near airport', 1),

-- Ethiopian address without GPS (for testing validation)
(NULL, NULL, 'AMHARA', 'Bahir Dar', 'Kebele 05', 'Ethiopian address without GPS', 1);

-- Create views for reporting
CREATE VIEW address_summary AS
SELECT 
    region,
    COUNT(*) as total_addresses,
    COUNT(CASE WHEN gps_lat IS NOT NULL AND gps_long IS NOT NULL THEN 1 END) as gps_addresses,
    COUNT(CASE WHEN region IS NOT NULL AND woreda IS NOT NULL AND kebele IS NOT NULL THEN 1 END) as ethiopian_addresses,
    COUNT(CASE WHEN gps_lat IS NOT NULL AND gps_long IS NOT NULL AND region IS NOT NULL AND woreda IS NOT NULL AND kebele IS NOT NULL THEN 1 END) as hybrid_addresses
FROM addresses 
WHERE active = true 
GROUP BY region;

CREATE VIEW postal_code_summary AS
SELECT 
    region,
    COUNT(*) as total_postal_codes,
    COUNT(CASE WHEN active = true THEN 1 END) as active_postal_codes
FROM postal_codes 
GROUP BY region;

-- Add comments for documentation
COMMENT ON TABLE postal_codes IS 'Ethiopian postal codes organized by region';
COMMENT ON TABLE addresses IS 'Addresses with GPS-first support and optional Ethiopian administrative structure';
COMMENT ON COLUMN addresses.gps_lat IS 'GPS latitude (required, -90 to 90)';
COMMENT ON COLUMN addresses.gps_long IS 'GPS longitude (required, -180 to 180)';
COMMENT ON COLUMN addresses.region IS 'Ethiopian administrative region (optional)';
COMMENT ON COLUMN addresses.woreda IS 'Woreda (district) within region (optional)';
COMMENT ON COLUMN addresses.kebele IS 'Kebele (neighborhood) within woreda (optional)';
COMMENT ON COLUMN addresses.description IS 'Free text description (e.g., "blue gate house near church")';
COMMENT ON COLUMN addresses.customer_id IS 'Customer who owns this address (nullable)';
COMMENT ON COLUMN addresses.partner_id IS 'Partner who owns this address (nullable)';
