-- Baseline to ensure Flyway can control future migrations
-- No destructive changes; create tables only if not exists for new setups

-- Partner
CREATE TABLE IF NOT EXISTS partners (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    city VARCHAR(50),
    logo_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    version BIGINT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_partners_name ON partners (name);
CREATE UNIQUE INDEX IF NOT EXISTS ux_partners_phone ON partners (phone);
CREATE UNIQUE INDEX IF NOT EXISTS ux_partners_email ON partners (email);

-- User
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    email VARCHAR(254),
    enabled BOOLEAN DEFAULT TRUE,
    version BIGINT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_user_username ON "user" (username);
CREATE UNIQUE INDEX IF NOT EXISTS ux_user_email ON "user" (email);

-- Driver
CREATE TABLE IF NOT EXISTS driver (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    phone_number VARCHAR(20),
    license_number VARCHAR(50),
    email VARCHAR(254),
    date_of_birth TIMESTAMP,
    profile_image_url VARCHAR(255),
    national_id_number VARCHAR(50),
    address VARCHAR(255),
    vehicle_type VARCHAR(50),
    vehicle_plate_number VARCHAR(20),
    vehicle_color VARCHAR(30),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    last_location_update TIMESTAMP,
    status VARCHAR(255) NOT NULL,
    active_deliveries INT DEFAULT 0,
    total_deliveries INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    registered_at TIMESTAMP,
    last_login_at TIMESTAMP,
    average_rating DOUBLE PRECISION DEFAULT 0,
    completed_deliveries_today INT DEFAULT 0,
    is_online BOOLEAN DEFAULT FALSE,
    is_available_for_delivery BOOLEAN DEFAULT TRUE,
    user_id BIGINT,
    version BIGINT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS ix_driver_phone_number ON driver (phone_number);
CREATE INDEX IF NOT EXISTS ix_driver_status ON driver (status);
CREATE INDEX IF NOT EXISTS ix_driver_user_id ON driver (user_id);

-- Delivery
CREATE TABLE IF NOT EXISTS delivery (
    id BIGSERIAL PRIMARY KEY,
    delivery_code VARCHAR(64),
    pickup_partner_id BIGINT,
    dropoff_partner_id BIGINT,
    driver_id BIGINT,
    status VARCHAR(32),
    pickup_time TIMESTAMPTZ,
    dropoff_time TIMESTAMPTZ,
    distance_in_km DOUBLE PRECISION,
    price DOUBLE PRECISION,
    dropoff_address VARCHAR(255) NOT NULL,
    partner_id BIGINT,
    version BIGINT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS ix_delivery_code ON delivery (delivery_code);
CREATE INDEX IF NOT EXISTS ix_delivery_status ON delivery (status);
CREATE INDEX IF NOT EXISTS ix_delivery_driver_id ON delivery (driver_id);

-- RefreshToken
CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_token_token ON refresh_token (token);


