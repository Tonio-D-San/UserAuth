-- =========================================
-- TRIGGER PER UPDATED_AT
-- =========================================
CREATE OR REPLACE FUNCTION refresh_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = (extract(epoch from now()) * 1000)::bigint;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================
-- ORDERS
-- =========================================
CREATE TABLE orders
(
    id         VARCHAR(32) PRIMARY KEY,
    uuid       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL DEFAULT true
);

CREATE TRIGGER orders_refresh_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- CALLS
-- =========================================
CREATE TABLE calls
(
    name        VARCHAR(255) NOT NULL PRIMARY KEY,
    description TEXT,
    duration    BIGINT,
    uuid        UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

CREATE TRIGGER calls_refresh_updated_at
    BEFORE UPDATE ON calls
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();
