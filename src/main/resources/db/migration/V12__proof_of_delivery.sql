-- Proof of Delivery Migration
-- V12__proof_of_delivery.sql

-- Create enum for proof of delivery types
CREATE TYPE proof_of_delivery_type AS ENUM ('PHOTO', 'SIGNATURE', 'BOTH');

-- Add proof of delivery columns to delivery table
ALTER TABLE delivery 
ADD COLUMN proof_of_delivery_type proof_of_delivery_type,
ADD COLUMN proof_of_delivery_url VARCHAR(500),
ADD COLUMN delivered_at TIMESTAMPTZ,
ADD COLUMN delivered_lat DOUBLE PRECISION,
ADD COLUMN delivered_long DOUBLE PRECISION;

-- Create indexes for proof of delivery queries
CREATE INDEX IF NOT EXISTS ix_delivery_proof_type ON delivery (proof_of_delivery_type);
CREATE INDEX IF NOT EXISTS ix_delivery_delivered_at ON delivery (delivered_at);
CREATE INDEX IF NOT EXISTS ix_delivery_proof_url ON delivery (proof_of_delivery_url) WHERE proof_of_delivery_url IS NOT NULL;

