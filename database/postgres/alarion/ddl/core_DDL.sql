DO $$ BEGIN
    CREATE TYPE realm_name_enum AS ENUM (
        'Coronor', 'Levalia', 'Malatea', 'Portumbria', 'Tal-Meridia', 'Valmora'
        );
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
-- =========================================
-- USERS (minimo indispensabile per FK)
-- =========================================
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(255) UNIQUE,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL DEFAULT true
);

DROP TRIGGER IF EXISTS users_refresh_updated_at ON users;
CREATE TRIGGER users_refresh_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


-- =========================================
-- REALMS
-- =========================================
CREATE TABLE IF NOT EXISTS realms
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    realm_name  realm_name_enum NOT NULL UNIQUE,
    description TEXT,
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

DROP TRIGGER IF EXISTS realms_refresh_updated_at ON realms;
CREATE TRIGGER realms_refresh_updated_at
    BEFORE UPDATE ON realms
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- FORMATIONS
-- =========================================
CREATE TABLE IF NOT EXISTS formations
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    code        VARCHAR(50) UNIQUE,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

DROP TRIGGER IF EXISTS formations_refresh_updated_at ON formations;
CREATE TRIGGER formations_refresh_updated_at
    BEFORE UPDATE ON formations
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


-- =========================================
-- CARDS (PS only)
-- =========================================
CREATE TABLE IF NOT EXISTS cards
(
    id               SERIAL PRIMARY KEY,
    uuid             UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    total_points     INTEGER NOT NULL DEFAULT 0,
    available_points INTEGER NOT NULL DEFAULT 0,
    used_points      INTEGER NOT NULL DEFAULT 0,
    created_at       BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at       BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active        BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT ck_cards_points_consistency CHECK (total_points = available_points + used_points),
    CONSTRAINT ck_cards_non_negative CHECK (total_points >= 0 AND available_points >= 0 AND used_points >= 0)
);

DROP TRIGGER IF EXISTS cards_refresh_updated_at ON cards;
CREATE TRIGGER cards_refresh_updated_at
    BEFORE UPDATE ON cards
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


-- =========================================
-- BAGS
-- =========================================
CREATE TABLE IF NOT EXISTS bags
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name        VARCHAR(100),
    description TEXT,
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

DROP TRIGGER IF EXISTS bags_refresh_updated_at ON bags;
CREATE TRIGGER bags_refresh_updated_at
    BEFORE UPDATE ON bags
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();
