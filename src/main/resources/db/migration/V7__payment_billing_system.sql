-- Payment & Billing System Migration
-- V7__payment_billing_system.sql

-- Create enums for payment and billing system
CREATE TYPE payment_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED');
CREATE TYPE payment_provider AS ENUM ('TELEBIRR', 'CBE_BIRR', 'M_BIRR', 'HELLOCASH', 'AMOLE');
CREATE TYPE invoice_status AS ENUM ('DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED');
CREATE TYPE payout_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED');
CREATE TYPE wallet_owner_type AS ENUM ('DRIVER', 'PARTNER');
CREATE TYPE transaction_type AS ENUM ('CREDIT', 'DEBIT');

-- Create payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    delivery_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'ETB',
    provider payment_provider NOT NULL,
    status payment_status NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(255),
    description TEXT,
    failure_reason TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_payments_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(id),
    CONSTRAINT uk_payments_transaction_ref UNIQUE (transaction_ref)
);

-- Create invoices table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    partner_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount > 0),
    due_date DATE NOT NULL,
    status invoice_status NOT NULL DEFAULT 'DRAFT',
    description TEXT,
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    paid_date DATE,
    payment_reference VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_invoices_partner FOREIGN KEY (partner_id) REFERENCES partners(id),
    CONSTRAINT chk_invoices_paid_amount CHECK (paid_amount >= 0),
    CONSTRAINT chk_invoices_paid_amount_not_exceed_total CHECK (paid_amount <= total_amount)
);

-- Create driver_earnings table
CREATE TABLE driver_earnings (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    delivery_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    payout_status payout_status NOT NULL DEFAULT 'PENDING',
    description TEXT,
    payout_reference VARCHAR(255),
    payout_date TIMESTAMP,
    failure_reason TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_driver_earnings_driver FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_driver_earnings_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(id),
    CONSTRAINT uk_driver_earnings_delivery UNIQUE (delivery_id)
);

-- Create partner_billings table
CREATE TABLE partner_billings (
    id BIGSERIAL PRIMARY KEY,
    partner_id BIGINT NOT NULL,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    status payment_status NOT NULL DEFAULT 'PENDING',
    description TEXT,
    payment_reference VARCHAR(255),
    payment_date TIMESTAMP,
    failure_reason TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_partner_billings_partner FOREIGN KEY (partner_id) REFERENCES partners(id),
    CONSTRAINT fk_partner_billings_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- Create wallets table
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    owner_type wallet_owner_type NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_wallets_owner UNIQUE (owner_id, owner_type)
);

-- Create wallet_transactions table
CREATE TABLE wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    transaction_type transaction_type NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    balance_before DECIMAL(10,2) NOT NULL CHECK (balance_before >= 0),
    balance_after DECIMAL(10,2) NOT NULL CHECK (balance_after >= 0),
    reference VARCHAR(255),
    description TEXT,
    metadata TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_wallet_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

-- Create indexes for performance
CREATE INDEX ix_payments_user_id ON payments(user_id) WHERE active = true;
CREATE INDEX ix_payments_delivery_id ON payments(delivery_id) WHERE active = true;
CREATE INDEX ix_payments_status ON payments(status) WHERE active = true;
CREATE INDEX ix_payments_provider ON payments(provider) WHERE active = true;
CREATE INDEX ix_payments_transaction_ref ON payments(transaction_ref) WHERE active = true;
CREATE INDEX ix_payments_created_at ON payments(created_at) WHERE active = true;

CREATE INDEX ix_invoices_partner_id ON invoices(partner_id) WHERE active = true;
CREATE INDEX ix_invoices_status ON invoices(status) WHERE active = true;
CREATE INDEX ix_invoices_due_date ON invoices(due_date) WHERE active = true;
CREATE INDEX ix_invoices_invoice_number ON invoices(invoice_number) WHERE active = true;
CREATE INDEX ix_invoices_created_at ON invoices(created_at) WHERE active = true;

CREATE INDEX ix_driver_earnings_driver_id ON driver_earnings(driver_id) WHERE active = true;
CREATE INDEX ix_driver_earnings_delivery_id ON driver_earnings(delivery_id) WHERE active = true;
CREATE INDEX ix_driver_earnings_payout_status ON driver_earnings(payout_status) WHERE active = true;
CREATE INDEX ix_driver_earnings_created_at ON driver_earnings(created_at) WHERE active = true;

CREATE INDEX ix_partner_billings_partner_id ON partner_billings(partner_id) WHERE active = true;
CREATE INDEX ix_partner_billings_invoice_id ON partner_billings(invoice_id) WHERE active = true;
CREATE INDEX ix_partner_billings_status ON partner_billings(status) WHERE active = true;
CREATE INDEX ix_partner_billings_created_at ON partner_billings(created_at) WHERE active = true;

CREATE INDEX ix_wallets_owner_id ON wallets(owner_id) WHERE active = true;
CREATE INDEX ix_wallets_owner_type ON wallets(owner_type) WHERE active = true;
CREATE INDEX ix_wallets_balance ON wallets(balance) WHERE active = true;
CREATE INDEX ix_wallets_created_at ON wallets(created_at) WHERE active = true;

CREATE INDEX ix_wallet_transactions_wallet_id ON wallet_transactions(wallet_id) WHERE active = true;
CREATE INDEX ix_wallet_transactions_type ON wallet_transactions(transaction_type) WHERE active = true;
CREATE INDEX ix_wallet_transactions_reference ON wallet_transactions(reference) WHERE active = true;
CREATE INDEX ix_wallet_transactions_created_at ON wallet_transactions(created_at) WHERE active = true;

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_driver_earnings_updated_at BEFORE UPDATE ON driver_earnings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_partner_billings_updated_at BEFORE UPDATE ON partner_billings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_wallets_updated_at BEFORE UPDATE ON wallets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_wallet_transactions_updated_at BEFORE UPDATE ON wallet_transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create views for reporting
CREATE VIEW payment_summary AS
SELECT 
    provider,
    status,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM payments 
WHERE active = true 
GROUP BY provider, status;

CREATE VIEW invoice_summary AS
SELECT 
    status,
    COUNT(*) as count,
    SUM(total_amount) as total_amount,
    SUM(paid_amount) as total_paid,
    AVG(total_amount) as avg_amount
FROM invoices 
WHERE active = true 
GROUP BY status;

CREATE VIEW driver_earnings_summary AS
SELECT 
    payout_status,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM driver_earnings 
WHERE active = true 
GROUP BY payout_status;

CREATE VIEW wallet_summary AS
SELECT 
    owner_type,
    COUNT(*) as count,
    SUM(balance) as total_balance,
    AVG(balance) as avg_balance
FROM wallets 
WHERE active = true 
GROUP BY owner_type;

CREATE VIEW wallet_transaction_summary AS
SELECT 
    transaction_type,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM wallet_transactions 
WHERE active = true 
GROUP BY transaction_type;

-- Insert sample data for testing
INSERT INTO wallets (owner_id, owner_type, balance, description) VALUES
(1, 'DRIVER', 1000.00, 'Sample driver wallet'),
(2, 'DRIVER', 500.00, 'Sample driver wallet 2'),
(1, 'PARTNER', 5000.00, 'Sample partner wallet'),
(2, 'PARTNER', 3000.00, 'Sample partner wallet 2');

-- Insert sample wallet transactions
INSERT INTO wallet_transactions (wallet_id, transaction_type, amount, balance_before, balance_after, reference, description) VALUES
(1, 'CREDIT', 1000.00, 0.00, 1000.00, 'INIT_001', 'Initial credit'),
(2, 'CREDIT', 500.00, 0.00, 500.00, 'INIT_002', 'Initial credit'),
(3, 'CREDIT', 5000.00, 0.00, 5000.00, 'INIT_003', 'Initial credit'),
(4, 'CREDIT', 3000.00, 0.00, 3000.00, 'INIT_004', 'Initial credit');

-- Add comments for documentation
COMMENT ON TABLE payments IS 'Stores payment transactions for deliveries';
COMMENT ON TABLE invoices IS 'Stores invoices for partner billing';
COMMENT ON TABLE driver_earnings IS 'Stores driver earnings from completed deliveries';
COMMENT ON TABLE partner_billings IS 'Stores partner billing records linked to invoices';
COMMENT ON TABLE wallets IS 'Stores digital wallets for drivers and partners';
COMMENT ON TABLE wallet_transactions IS 'Stores all wallet transactions with balance history';

COMMENT ON COLUMN payments.provider IS 'Ethiopian payment provider (TeleBirr, CBE Birr, etc.)';
COMMENT ON COLUMN payments.currency IS 'Currency code (default ETB for Ethiopian Birr)';
COMMENT ON COLUMN invoices.invoice_number IS 'Unique invoice number generated by system';
COMMENT ON COLUMN driver_earnings.amount IS 'Driver earnings amount (typically 75% of delivery price)';
COMMENT ON COLUMN wallets.owner_type IS 'Type of wallet owner (DRIVER or PARTNER)';
COMMENT ON COLUMN wallet_transactions.balance_before IS 'Wallet balance before transaction';
COMMENT ON COLUMN wallet_transactions.balance_after IS 'Wallet balance after transaction';
