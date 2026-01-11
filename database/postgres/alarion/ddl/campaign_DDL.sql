-- =========================================
-- BASE TRIGGERS PER AUDIT
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
-- CAMPAIGNS
-- =========================================
CREATE TABLE campaigns
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID         NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  BIGINT       NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT       NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN      NOT NULL        DEFAULT true
);

CREATE TRIGGER campaigns_refresh_updated_at
    BEFORE UPDATE
    ON campaigns
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

CREATE TABLE IF NOT EXISTS events
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    campaign_id INTEGER NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    name        VARCHAR(150) NOT NULL,
    location    VARCHAR(255),
    start_at    BIGINT,
    end_at      BIGINT,
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX IF NOT EXISTS idx_events_campaign_id ON events(campaign_id);

CREATE TRIGGER events_refresh_updated_at
    BEFORE UPDATE ON events
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


CREATE TABLE IF NOT EXISTS event_days
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    event_id   INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    day_number INTEGER NOT NULL,
    day_date   BIGINT,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT ux_event_day UNIQUE (event_id, day_number)
);

CREATE INDEX IF NOT EXISTS idx_event_days_event_id ON event_days(event_id);

CREATE TRIGGER event_days_refresh_updated_at
    BEFORE UPDATE ON event_days
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


CREATE TYPE IF NOT EXISTS attendance_status_enum AS ENUM ('present', 'absent', 'late', 'staff');

CREATE TABLE IF NOT EXISTS character_attendance
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    character_id INTEGER NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    event_day_id INTEGER NOT NULL REFERENCES event_days(id) ON DELETE CASCADE,
    status       attendance_status_enum NOT NULL DEFAULT 'present',
    created_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT ux_character_day UNIQUE (character_id, event_day_id)
);

CREATE INDEX IF NOT EXISTS idx_attendance_character_id ON character_attendance(character_id);
CREATE INDEX IF NOT EXISTS idx_attendance_event_day_id ON character_attendance(event_day_id);

CREATE TRIGGER character_attendance_refresh_updated_at
    BEFORE UPDATE ON character_attendance
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();
