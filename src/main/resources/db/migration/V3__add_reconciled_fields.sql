-- Additive, non-breaking columns to reconcile with DashCraft domain

-- Driver additions
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='zone') THEN
        ALTER TABLE driver ADD COLUMN zone VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='last_speed') THEN
        ALTER TABLE driver ADD COLUMN last_speed DOUBLE PRECISION;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='last_heading') THEN
        ALTER TABLE driver ADD COLUMN last_heading DOUBLE PRECISION;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='last_seen_at') THEN
        ALTER TABLE driver ADD COLUMN last_seen_at TIMESTAMPTZ;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='max_capacity') THEN
        ALTER TABLE driver ADD COLUMN max_capacity INTEGER;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='driver' AND column_name='current_load') THEN
        ALTER TABLE driver ADD COLUMN current_load INTEGER;
    END IF;
END $$;

-- User additions
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='phone') THEN
        ALTER TABLE "user" ADD COLUMN phone VARCHAR(30);
        CREATE UNIQUE INDEX IF NOT EXISTS ux_user_phone ON "user"(phone);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='full_name') THEN
        ALTER TABLE "user" ADD COLUMN full_name VARCHAR(200);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='region') THEN
        ALTER TABLE "user" ADD COLUMN region VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='user' AND column_name='language') THEN
        ALTER TABLE "user" ADD COLUMN language VARCHAR(10);
    END IF;
END $$;

-- Delivery additions
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='delivery' AND column_name='pickup_region') THEN
        ALTER TABLE delivery ADD COLUMN pickup_region VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='delivery' AND column_name='dropoff_region') THEN
        ALTER TABLE delivery ADD COLUMN dropoff_region VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='delivery' AND column_name='distance_km') THEN
        ALTER TABLE delivery ADD COLUMN distance_km DOUBLE PRECISION;
    END IF;
END $$;

-- Partner additions
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='business_type') THEN
        ALTER TABLE partners ADD COLUMN business_type VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='kebele') THEN
        ALTER TABLE partners ADD COLUMN kebele VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='woreda') THEN
        ALTER TABLE partners ADD COLUMN woreda VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='region') THEN
        ALTER TABLE partners ADD COLUMN region VARCHAR(100);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='verified') THEN
        ALTER TABLE partners ADD COLUMN verified BOOLEAN;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='rating') THEN
        ALTER TABLE partners ADD COLUMN rating DOUBLE PRECISION;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='partners' AND column_name='total_orders') THEN
        ALTER TABLE partners ADD COLUMN total_orders INTEGER;
    END IF;
END $$;



