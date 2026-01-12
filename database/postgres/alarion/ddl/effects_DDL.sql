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
-- EFFECTS
-- =========================================
CREATE TABLE effects
(
    prefix     VARCHAR(255) NOT NULL,
    call       VARCHAR(255) NOT NULL,
    duration   BIGINT,
    uuid       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL DEFAULT true,
    PRIMARY KEY (prefix, call)
);

CREATE TRIGGER effects_refresh_updated_at
    BEFORE UPDATE ON effects
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();
