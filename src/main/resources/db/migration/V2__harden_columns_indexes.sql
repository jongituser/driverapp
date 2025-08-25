-- Non-destructive alters to align with hardened entities

-- Driver
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='version'
    ) THEN
        ALTER TABLE driver ADD COLUMN version BIGINT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='created_at'
    ) THEN
        ALTER TABLE driver ADD COLUMN created_at TIMESTAMPTZ;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='updated_at'
    ) THEN
        ALTER TABLE driver ADD COLUMN updated_at TIMESTAMPTZ;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS ix_driver_phone_number ON driver (phone_number);
CREATE INDEX IF NOT EXISTS ix_driver_status ON driver (status);
CREATE INDEX IF NOT EXISTS ix_driver_user_id ON driver (user_id);

-- User
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='version'
    ) THEN
        ALTER TABLE "user" ADD COLUMN version BIGINT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='created_at'
    ) THEN
        ALTER TABLE "user" ADD COLUMN created_at TIMESTAMPTZ;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='updated_at'
    ) THEN
        ALTER TABLE "user" ADD COLUMN updated_at TIMESTAMPTZ;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_user_username ON "user" (username);
CREATE UNIQUE INDEX IF NOT EXISTS ux_user_email ON "user" (email);

-- Partner
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='version'
    ) THEN
        ALTER TABLE partners ADD COLUMN version BIGINT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='created_at'
    ) THEN
        ALTER TABLE partners ADD COLUMN created_at TIMESTAMPTZ;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='updated_at'
    ) THEN
        ALTER TABLE partners ADD COLUMN updated_at TIMESTAMPTZ;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS ix_partners_name ON partners (name);
CREATE INDEX IF NOT EXISTS ix_partners_phone ON partners (phone);
CREATE INDEX IF NOT EXISTS ix_partners_email ON partners (email);

-- Delivery
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='delivery' AND column_name='version'
    ) THEN
        ALTER TABLE delivery ADD COLUMN version BIGINT;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='delivery' AND column_name='created_at'
    ) THEN
        ALTER TABLE delivery ADD COLUMN created_at TIMESTAMPTZ;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='delivery' AND column_name='updated_at'
    ) THEN
        ALTER TABLE delivery ADD COLUMN updated_at TIMESTAMPTZ;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS ix_delivery_code ON delivery (delivery_code);
CREATE INDEX IF NOT EXISTS ix_delivery_status ON delivery (status);
CREATE INDEX IF NOT EXISTS ix_delivery_driver_id ON delivery (driver_id);

-- Refresh Token
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='refresh_token' AND column_name='created_at'
    ) THEN
        ALTER TABLE refresh_token ADD COLUMN created_at TIMESTAMPTZ;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_token_token ON refresh_token (token);



