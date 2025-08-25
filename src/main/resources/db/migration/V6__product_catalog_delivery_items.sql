-- V6__product_catalog_delivery_items.sql
-- Product Catalog and Delivery Items Migration

-- Create products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    unit VARCHAR(20) NOT NULL,
    supplier_id BIGINT,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

-- Create delivery_items table
CREATE TABLE delivery_items (
    id BIGSERIAL PRIMARY KEY,
    delivery_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    total DECIMAL(10,2) NOT NULL CHECK (total > 0),
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_delivery_items_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT uk_delivery_product UNIQUE (delivery_id, product_id)
);

-- Create indexes for products table
CREATE INDEX ix_product_sku ON products(sku);
CREATE INDEX ix_product_category ON products(category);
CREATE INDEX ix_product_name ON products(name);
CREATE INDEX ix_product_supplier_id ON products(supplier_id);
CREATE INDEX ix_product_active ON products(active);

-- Create indexes for delivery_items table
CREATE INDEX ix_delivery_item_delivery_id ON delivery_items(delivery_id);
CREATE INDEX ix_delivery_item_product_id ON delivery_items(product_id);
CREATE INDEX ix_delivery_item_active ON delivery_items(active);

-- Add comments to tables
COMMENT ON TABLE products IS 'Product catalog with Ethiopian products';
COMMENT ON TABLE delivery_items IS 'Items in deliveries linking products to deliveries';

-- Add comments to columns
COMMENT ON COLUMN products.name IS 'Product name';
COMMENT ON COLUMN products.category IS 'Product category (Injera, Coffee, Spices, etc.)';
COMMENT ON COLUMN products.sku IS 'Stock Keeping Unit - unique product identifier';
COMMENT ON COLUMN products.price IS 'Product price in ETB';
COMMENT ON COLUMN products.unit IS 'Unit of measurement (kg, piece, liter, etc.)';
COMMENT ON COLUMN products.supplier_id IS 'Reference to supplier';
COMMENT ON COLUMN products.description IS 'Product description';
COMMENT ON COLUMN products.active IS 'Whether the product is active';

COMMENT ON COLUMN delivery_items.delivery_id IS 'Reference to delivery';
COMMENT ON COLUMN delivery_items.product_id IS 'Reference to product';
COMMENT ON COLUMN delivery_items.quantity IS 'Quantity of product in delivery';
COMMENT ON COLUMN delivery_items.price IS 'Price per unit at time of delivery';
COMMENT ON COLUMN delivery_items.total IS 'Total amount for this item (quantity * price)';
COMMENT ON COLUMN delivery_items.active IS 'Whether the delivery item is active';

-- Insert sample Ethiopian products
INSERT INTO products (name, category, sku, price, unit, description) VALUES
-- Injera Products
('Traditional Injera', 'Injera', 'INJ-001', 25.00, 'piece', 'Traditional Ethiopian flatbread made from teff flour'),
('Teff Injera', 'Injera', 'INJ-002', 30.00, 'piece', 'Premium injera made from pure teff flour'),
('Mixed Grain Injera', 'Injera', 'INJ-003', 20.00, 'piece', 'Injera made from mixed grains (teff, barley, wheat)'),

-- Coffee Products
('Ethiopian Yirgacheffe Coffee', 'Coffee', 'COF-001', 150.00, 'kg', 'Premium single-origin coffee from Yirgacheffe region'),
('Sidamo Coffee Beans', 'Coffee', 'COF-002', 120.00, 'kg', 'High-quality coffee beans from Sidamo region'),
('Harar Coffee', 'Coffee', 'COF-003', 180.00, 'kg', 'Traditional Harar coffee with distinctive flavor'),
('Ground Coffee', 'Coffee', 'COF-004', 140.00, 'kg', 'Pre-ground Ethiopian coffee for convenience'),

-- Spices
('Berbere Spice', 'Spices', 'SPC-001', 80.00, 'kg', 'Traditional Ethiopian spice blend for meat dishes'),
('Mitmita Spice', 'Spices', 'SPC-002', 90.00, 'kg', 'Hot spice blend for meat and vegetables'),
('Cardamom', 'Spices', 'SPC-003', 200.00, 'kg', 'Premium Ethiopian cardamom pods'),
('Cinnamon', 'Spices', 'SPC-004', 120.00, 'kg', 'Ethiopian cinnamon sticks'),
('Turmeric', 'Spices', 'SPC-005', 60.00, 'kg', 'Ground turmeric for cooking and health benefits'),

-- Grains and Legumes
('Teff Grain', 'Grains', 'GRN-001', 100.00, 'kg', 'Ancient grain native to Ethiopia'),
('Chickpeas', 'Legumes', 'LEG-001', 45.00, 'kg', 'Fresh Ethiopian chickpeas'),
('Lentils', 'Legumes', 'LEG-002', 50.00, 'kg', 'Red lentils for traditional dishes'),
('Barley', 'Grains', 'GRN-002', 35.00, 'kg', 'Ethiopian barley for traditional beer and food'),

-- Honey and Sweeteners
('Ethiopian Honey', 'Honey', 'HON-001', 300.00, 'kg', 'Pure Ethiopian honey from local beekeepers'),
('Tej Honey Wine', 'Beverages', 'BEV-001', 250.00, 'liter', 'Traditional Ethiopian honey wine'),

-- Traditional Foods
('Shiro Powder', 'Traditional', 'TRD-001', 70.00, 'kg', 'Ground chickpea powder for shiro stew'),
('Kolo', 'Snacks', 'SNK-001', 40.00, 'kg', 'Roasted barley snack'),
('Dabo Kolo', 'Snacks', 'SNK-002', 35.00, 'kg', 'Traditional Ethiopian bread snack'),

-- Fresh Produce
('Tomatoes', 'Vegetables', 'VEG-001', 15.00, 'kg', 'Fresh Ethiopian tomatoes'),
('Onions', 'Vegetables', 'VEG-002', 12.00, 'kg', 'Local Ethiopian onions'),
('Potatoes', 'Vegetables', 'VEG-003', 18.00, 'kg', 'Fresh Ethiopian potatoes'),
('Carrots', 'Vegetables', 'VEG-004', 20.00, 'kg', 'Fresh Ethiopian carrots'),

-- Dairy Products
('Ayib Cheese', 'Dairy', 'DAI-001', 80.00, 'kg', 'Traditional Ethiopian cottage cheese'),
('Butter', 'Dairy', 'DAI-002', 120.00, 'kg', 'Traditional Ethiopian butter'),

-- Beverages
('Coffee Beans Green', 'Coffee', 'COF-005', 100.00, 'kg', 'Unroasted green coffee beans'),
('Tea Leaves', 'Beverages', 'BEV-002', 60.00, 'kg', 'Ethiopian tea leaves'),

-- Pharmaceuticals (Basic)
('Paracetamol', 'Pharmaceuticals', 'PHA-001', 5.00, 'pack', 'Basic pain relief medication'),
('Amoxicillin', 'Pharmaceuticals', 'PHA-002', 15.00, 'pack', 'Antibiotic medication'),
('Vitamin C', 'Pharmaceuticals', 'PHA-003', 8.00, 'pack', 'Vitamin C supplements'),

-- Electronics (Basic)
('Mobile Phone Charger', 'Electronics', 'ELE-001', 150.00, 'piece', 'Universal mobile phone charger'),
('LED Bulb', 'Electronics', 'ELE-002', 25.00, 'piece', 'Energy-efficient LED light bulb'),
('Power Bank', 'Electronics', 'ELE-003', 200.00, 'piece', 'Portable power bank for mobile devices');

-- Update some products to have suppliers (assuming suppliers exist from V4 migration)
UPDATE products SET supplier_id = 1 WHERE sku IN ('INJ-001', 'INJ-002', 'INJ-003');
UPDATE products SET supplier_id = 2 WHERE sku IN ('COF-001', 'COF-002', 'COF-003');
UPDATE products SET supplier_id = 3 WHERE sku IN ('SPC-001', 'SPC-002', 'SPC-003');
UPDATE products SET supplier_id = 4 WHERE sku IN ('GRN-001', 'LEG-001', 'LEG-002');
UPDATE products SET supplier_id = 5 WHERE sku IN ('HON-001', 'BEV-001');

-- Insert sample delivery items (assuming deliveries exist)
-- Note: These will be created when actual deliveries are made
-- This is just a sample for demonstration
INSERT INTO delivery_items (delivery_id, product_id, quantity, price, total) VALUES
(1, 1, 5, 25.00, 125.00),
(1, 4, 2, 150.00, 300.00),
(1, 8, 1, 80.00, 80.00),
(2, 2, 3, 30.00, 90.00),
(2, 5, 1, 120.00, 120.00),
(2, 9, 2, 90.00, 180.00);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_delivery_items_updated_at BEFORE UPDATE ON delivery_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
