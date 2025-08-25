-- Analytics System Migration
-- V10__analytics_system.sql

CREATE TYPE analytics_record_type AS ENUM ('DELIVERY', 'PAYMENT', 'INVENTORY');

CREATE TABLE analytics_records (
    id BIGSERIAL PRIMARY KEY,
    type analytics_record_type NOT NULL,
    entity_id BIGINT NOT NULL,
    data JSONB NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX idx_analytics_records_type ON analytics_records(type);
CREATE INDEX idx_analytics_records_entity_id ON analytics_records(entity_id);
CREATE INDEX idx_analytics_records_created_at ON analytics_records(created_at);
CREATE INDEX idx_analytics_records_type_created_at ON analytics_records(type, created_at);
CREATE INDEX idx_analytics_records_active ON analytics_records(active);

-- Composite index for common queries
CREATE INDEX idx_analytics_records_type_entity_active ON analytics_records(type, entity_id, active);

