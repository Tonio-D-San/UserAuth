-- 00_base.sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION refresh_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = (extract(epoch from now()) * 1000)::bigint;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$ BEGIN
    CREATE TYPE attendance_status_enum AS ENUM ('present', 'absent');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE card_tx_source_enum AS ENUM ('event_day');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
