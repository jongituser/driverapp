-- Inventory Management System Migration
-- V4__inventory_management.sql

-- Suppliers table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(254),
    address VARCHAR(500),
    city VARCHAR(100),
    region VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    verified BOOLEAN DEFAULT FALSE,
    partner_id BIGINT,
    version BIGINT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_suppliers_partner FOREIGN KEY (partner_id) REFERENCES partners(id)
);

-- Create indexes for suppliers
CREATE INDEX IF NOT EXISTS ix_supplier_name ON suppliers (name);
CREATE INDEX IF NOT EXISTS ix_supplier_phone ON suppliers (phone);
CREATE INDEX IF NOT EXISTS ix_supplier_email ON suppliers (email);
CREATE INDEX IF NOT EXISTS ix_supplier_partner_id ON suppliers (partner_id);

-- Create unique constraints for suppliers
CREATE UNIQUE INDEX IF NOT EXISTS ux_suppliers_name ON suppliers (name);
CREATE UNIQUE INDEX IF NOT EXISTS ux_suppliers_phone ON suppliers (phone);
CREATE UNIQUE INDEX IF NOT EXISTS ux_suppliers_email ON suppliers (email) WHERE email IS NOT NULL;

-- Inventory Items table
CREATE TABLE IF NOT EXISTS inventory_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    quantity INTEGER NOT NULL,
    unit VARCHAR(20) NOT NULL,
    minimum_stock_threshold INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    total_value DECIMAL(10,2),
    batch_number VARCHAR(100),
    expiry_date DATE,
    description VARCHAR(500),
    image_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    low_stock_alert BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE,
    partner_id BIGINT,
    supplier_id BIGINT,
    version BIGINT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_inventory_items_partner FOREIGN KEY (partner_id) REFERENCES partners(id),
    CONSTRAINT fk_inventory_items_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- Create indexes for inventory items
CREATE INDEX IF NOT EXISTS ix_inventory_sku ON inventory_items (sku);
CREATE INDEX IF NOT EXISTS ix_inventory_name ON inventory_items (name);
CREATE INDEX IF NOT EXISTS ix_inventory_category ON inventory_items (category);
CREATE INDEX IF NOT EXISTS ix_inventory_partner_id ON inventory_items (partner_id);
CREATE INDEX IF NOT EXISTS ix_inventory_supplier_id ON inventory_items (supplier_id);
CREATE INDEX IF NOT EXISTS ix_inventory_expiry_date ON inventory_items (expiry_date);
CREATE INDEX IF NOT EXISTS ix_inventory_batch_number ON inventory_items (batch_number);
CREATE INDEX IF NOT EXISTS ix_inventory_quantity ON inventory_items (quantity);

-- Inventory Logs table
CREATE TABLE IF NOT EXISTS inventory_logs (
    id BIGSERIAL PRIMARY KEY,
    log_type VARCHAR(50) NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    quantity_before INTEGER NOT NULL,
    quantity_after INTEGER NOT NULL,
    quantity_changed INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    total_value DECIMAL(10,2),
    reason VARCHAR(200),
    notes VARCHAR(500),
    partner_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMPTZ,
    CONSTRAINT fk_inventory_logs_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id),
    CONSTRAINT fk_inventory_logs_partner FOREIGN KEY (partner_id) REFERENCES partners(id),
    CONSTRAINT fk_inventory_logs_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

-- Create indexes for inventory logs
CREATE INDEX IF NOT EXISTS ix_inventory_log_item_id ON inventory_logs (inventory_item_id);
CREATE INDEX IF NOT EXISTS ix_inventory_log_type ON inventory_logs (log_type);
CREATE INDEX IF NOT EXISTS ix_inventory_log_created_at ON inventory_logs (created_at);
CREATE INDEX IF NOT EXISTS ix_inventory_log_partner_id ON inventory_logs (partner_id);

-- Add comments for documentation
COMMENT ON TABLE suppliers IS 'Suppliers for inventory items with partner/location association';
COMMENT ON TABLE inventory_items IS 'Inventory items with multi-location support and expiry tracking';
COMMENT ON TABLE inventory_logs IS 'Audit trail for all inventory movements and changes';

COMMENT ON COLUMN suppliers.name IS 'Supplier business name';
COMMENT ON COLUMN suppliers.phone IS 'Primary contact phone number';
COMMENT ON COLUMN suppliers.email IS 'Primary contact email address';
COMMENT ON COLUMN suppliers.verified IS 'Whether supplier has been verified by admin';
COMMENT ON COLUMN suppliers.partner_id IS 'Associated partner/location';

COMMENT ON COLUMN inventory_items.name IS 'Item display name';
COMMENT ON COLUMN inventory_items.sku IS 'Stock Keeping Unit - unique identifier';
COMMENT ON COLUMN inventory_items.category IS 'Item category for organization';
COMMENT ON COLUMN inventory_items.quantity IS 'Current stock quantity';
COMMENT ON COLUMN inventory_items.unit IS 'Unit of measurement (pieces, kg, liters, etc.)';
COMMENT ON COLUMN inventory_items.minimum_stock_threshold IS 'Minimum quantity before low stock alert';
COMMENT ON COLUMN inventory_items.batch_number IS 'Batch/lot number for tracking';
COMMENT ON COLUMN inventory_items.expiry_date IS 'Expiration date for perishable items';
COMMENT ON COLUMN inventory_items.low_stock_alert IS 'Whether item is currently below threshold';
COMMENT ON COLUMN inventory_items.expired IS 'Whether item has expired';
COMMENT ON COLUMN inventory_items.partner_id IS 'Warehouse/location where item is stored';
COMMENT ON COLUMN inventory_items.supplier_id IS 'Supplier who provides this item';

COMMENT ON COLUMN inventory_logs.log_type IS 'Type of inventory movement (STOCK_IN, STOCK_OUT, etc.)';
COMMENT ON COLUMN inventory_logs.quantity_before IS 'Quantity before the change';
COMMENT ON COLUMN inventory_logs.quantity_after IS 'Quantity after the change';
COMMENT ON COLUMN inventory_logs.quantity_changed IS 'Absolute value of quantity change';
COMMENT ON COLUMN inventory_logs.reason IS 'Reason for the stock movement';
COMMENT ON COLUMN inventory_logs.notes IS 'Additional notes about the movement';
COMMENT ON COLUMN inventory_logs.partner_id IS 'Location where change occurred';
COMMENT ON COLUMN inventory_logs.user_id IS 'User who made the change';

-- Insert sample data for testing (optional)
-- Sample suppliers
INSERT INTO suppliers (name, phone, email, address, city, region, active, verified, partner_id, created_at, updated_at)
VALUES 
    ('Ethiopian Pharmaceuticals', '+251911234567', 'info@ethpharma.com', 'Bole Road, Addis Ababa', 'Addis Ababa', 'Addis Ababa', true, true, 1, NOW(), NOW()),
    ('Addis Medical Supplies', '+251922345678', 'contact@addismedical.com', 'Kazanchis, Addis Ababa', 'Addis Ababa', 'Addis Ababa', true, true, 1, NOW(), NOW()),
    ('Dire Dawa Healthcare', '+251933456789', 'info@diredawahealth.com', 'Central Market, Dire Dawa', 'Dire Dawa', 'Dire Dawa', true, false, 2, NOW(), NOW());

-- Sample inventory items
INSERT INTO inventory_items (name, category, sku, quantity, unit, minimum_stock_threshold, unit_price, total_value, batch_number, expiry_date, description, active, low_stock_alert, expired, partner_id, supplier_id, created_at, updated_at)
VALUES 
    ('Paracetamol 500mg', 'Pain Relief', 'PAR-500-001', 150, 'pieces', 20, 2.50, 375.00, 'BATCH-2024-001', '2025-12-31', 'Standard pain relief medication', true, false, false, 1, 1, NOW(), NOW()),
    ('Amoxicillin 250mg', 'Antibiotics', 'AMX-250-001', 8, 'pieces', 10, 15.00, 120.00, 'BATCH-2024-002', '2024-06-30', 'Broad-spectrum antibiotic', true, true, false, 1, 1, NOW(), NOW()),
    ('Vitamin C 1000mg', 'Vitamins', 'VIT-C-1000-001', 200, 'pieces', 15, 5.00, 1000.00, 'BATCH-2024-003', '2025-03-15', 'Immune system support', true, false, false, 1, 2, NOW(), NOW()),
    ('Ibuprofen 400mg', 'Pain Relief', 'IBU-400-001', 0, 'pieces', 25, 3.00, 0.00, 'BATCH-2024-004', '2024-08-31', 'Anti-inflammatory pain relief', true, true, false, 2, 3, NOW(), NOW()),
    ('Expired Test Item', 'Test Category', 'EXP-TEST-001', 50, 'pieces', 10, 1.00, 50.00, 'BATCH-2023-001', '2023-12-31', 'Test item for expiry tracking', true, false, true, 1, 1, NOW(), NOW());

-- Sample inventory logs
INSERT INTO inventory_logs (log_type, inventory_item_id, quantity_before, quantity_after, quantity_changed, unit_price, total_value, reason, partner_id, created_at)
VALUES 
    ('INITIAL_STOCK', 1, 0, 150, 150, 2.50, 375.00, 'Initial stock setup', 1, NOW()),
    ('INITIAL_STOCK', 2, 0, 8, 8, 15.00, 120.00, 'Initial stock setup', 1, NOW()),
    ('INITIAL_STOCK', 3, 0, 200, 200, 5.00, 1000.00, 'Initial stock setup', 1, NOW()),
    ('INITIAL_STOCK', 4, 0, 0, 0, 3.00, 0.00, 'Initial stock setup', 2, NOW()),
    ('INITIAL_STOCK', 5, 0, 50, 50, 1.00, 50.00, 'Initial stock setup', 1, NOW());
