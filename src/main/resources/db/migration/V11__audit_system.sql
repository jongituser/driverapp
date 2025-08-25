-- Create audit action enum
CREATE TYPE audit_action AS ENUM (
    'CREATE',
    'UPDATE',
    'DELETE',
    'ACTIVATE',
    'DEACTIVATE',
    'VERIFY',
    'UNVERIFY',
    'ASSIGN',
    'UNASSIGN',
    'COMPLETE',
    'CANCEL',
    'PAYMENT_INITIATED',
    'PAYMENT_COMPLETED',
    'PAYMENT_FAILED',
    'LOGIN',
    'LOGOUT',
    'PASSWORD_CHANGE',
    'ROLE_CHANGE',
    'PERMISSION_CHANGE'
);

-- Create audit entity type enum
CREATE TYPE audit_entity_type AS ENUM (
    'USER',
    'DRIVER',
    'CUSTOMER',
    'PARTNER',
    'DELIVERY',
    'PAYMENT',
    'INVENTORY_ITEM',
    'PRODUCT',
    'SUPPLIER',
    'ADDRESS',
    'POSTAL_CODE',
    'ANALYTICS_RECORD',
    'NOTIFICATION',
    'NOTIFICATION_TEMPLATE',
    'WALLET',
    'WALLET_TRANSACTION',
    'INVOICE',
    'DRIVER_EARNING',
    'PARTNER_BILLING'
);

-- Create audit_logs table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type audit_entity_type NOT NULL,
    entity_id BIGINT NOT NULL,
    action audit_action NOT NULL,
    user_id BIGINT,
    user_email VARCHAR(255),
    before_snapshot JSONB,
    after_snapshot JSONB,
    changes_summary TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_audit_logs_entity_type_entity_id ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_active ON audit_logs(active);
CREATE INDEX idx_audit_logs_entity_type_created_at ON audit_logs(entity_type, created_at);
CREATE INDEX idx_audit_logs_user_id_created_at ON audit_logs(user_id, created_at);

-- Create composite index for compliance queries
CREATE INDEX idx_audit_logs_compliance ON audit_logs(entity_type, action, created_at, active);

-- Add comments for documentation
COMMENT ON TABLE audit_logs IS 'Audit trail for all entity changes in the system';
COMMENT ON COLUMN audit_logs.entity_type IS 'Type of entity being audited';
COMMENT ON COLUMN audit_logs.entity_id IS 'ID of the entity being audited';
COMMENT ON COLUMN audit_logs.action IS 'Action performed on the entity';
COMMENT ON COLUMN audit_logs.user_id IS 'ID of the user who performed the action';
COMMENT ON COLUMN audit_logs.user_email IS 'Email of the user who performed the action';
COMMENT ON COLUMN audit_logs.before_snapshot IS 'JSON snapshot of entity state before the action';
COMMENT ON COLUMN audit_logs.after_snapshot IS 'JSON snapshot of entity state after the action';
COMMENT ON COLUMN audit_logs.changes_summary IS 'Human-readable summary of changes made';
COMMENT ON COLUMN audit_logs.ip_address IS 'IP address of the user who performed the action';
COMMENT ON COLUMN audit_logs.user_agent IS 'User agent string of the client';
COMMENT ON COLUMN audit_logs.active IS 'Whether this audit log entry is active';
COMMENT ON COLUMN audit_logs.version IS 'Optimistic locking version';
COMMENT ON COLUMN audit_logs.created_at IS 'Timestamp when the audit log was created';

