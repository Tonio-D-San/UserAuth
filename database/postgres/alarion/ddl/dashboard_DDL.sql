-- =========================================
-- ENUM DEFINITIONS
-- =========================================
CREATE TYPE money_name_enum AS ENUM ('copper', 'silver', 'gold');
CREATE TYPE reagent_name_enum AS ENUM ('Reagent A, Reagent B');
CREATE TYPE realm_name_enum AS ENUM (
    'Coronor', 'Levalia', 'Malatea', 'Portumbria', 'Tal-Meridia', 'Valmora'
    );

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
-- BAG
-- =========================================
CREATE TABLE bags
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_at BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL        DEFAULT true
);

CREATE TRIGGER bags_refresh_updated_at
    BEFORE UPDATE
    ON bags
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- CARD
-- =========================================
CREATE TABLE cards
(
    id               SERIAL PRIMARY KEY,
    uuid             UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_at       BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at       BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active        BOOLEAN NOT NULL        DEFAULT true,
    total_points     INTEGER,
    available_points INTEGER,
    used_points      INTEGER,
    CHECK (total_points = available_points + used_points)
);

CREATE TRIGGER cards_refresh_updated_at
    BEFORE UPDATE
    ON cards
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

CREATE TYPE card_tx_source_enum AS ENUM ('creation', 'event_day', 'ability_purchase', 'gm_adjust');

CREATE TABLE IF NOT EXISTS card_transactions
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    card_id      INTEGER NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    source       card_tx_source_enum NOT NULL,
    amount       INTEGER NOT NULL,
    reference_id INTEGER,
    note         TEXT,
    created_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX IF NOT EXISTS idx_card_tx_card_id ON card_transactions(card_id);
CREATE INDEX IF NOT EXISTS idx_card_tx_source ON card_transactions(source);

CREATE TRIGGER card_transactions_refresh_updated_at
    BEFORE UPDATE ON card_transactions FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


-- =========================================
-- ABILITY
-- =========================================
CREATE TABLE ability
(
    id               SERIAL PRIMARY KEY,
    uuid             UUID         NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    code             VARCHAR(50) UNIQUE,
    name             VARCHAR(255) NOT NULL,
    description_key  VARCHAR(255) NOT NULL,
    type             VARCHAR(50),
    requirement_type VARCHAR(10),
    created_at       BIGINT       NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at       BIGINT       NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active        BOOLEAN      NOT NULL        DEFAULT true
);

CREATE TRIGGER ability_refresh_updated_at
    BEFORE UPDATE
    ON ability
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

CREATE UNIQUE INDEX IF NOT EXISTS ux_ability_uuid ON ability(uuid);

-- =========================================
-- NOTES
-- =========================================
CREATE TABLE notes
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    note       TEXT,
    created_at BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL        DEFAULT true
);

CREATE TRIGGER notes_refresh_updated_at
    BEFORE UPDATE
    ON notes
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- ABILITY_NOTES (ManyToMany)
-- =========================================
CREATE TABLE ability_notes
(
    ability_id INT NOT NULL,
    note_id    INT NOT NULL,
    PRIMARY KEY (ability_id, note_id),
    CONSTRAINT fk_ability_notes_ability
        FOREIGN KEY (ability_id) REFERENCES ability (id) ON DELETE CASCADE,
    CONSTRAINT fk_ability_notes_note
        FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE
);

CREATE INDEX idx_ability_notes_ability_id ON ability_notes (ability_id);
CREATE INDEX idx_ability_notes_note_id ON ability_notes (note_id);

-- =========================================
-- ABILITY_REQUIREMENTS
-- =========================================
CREATE TABLE ability_requirements
(
    ability_id     INT NOT NULL,
    requirement_id INT NOT NULL,
    PRIMARY KEY (ability_id, requirement_id),
    CONSTRAINT fk_ability_requirements_ability
        FOREIGN KEY (ability_id) REFERENCES ability (id) ON DELETE CASCADE,
    CONSTRAINT fk_ability_requirements_requirement
        FOREIGN KEY (requirement_id) REFERENCES ability (id) ON DELETE CASCADE
);

CREATE INDEX idx_ability_requirements_ability_id ON ability_requirements (ability_id);
CREATE INDEX idx_ability_requirements_requirements ON ability_requirements (requirement_id);

-- =========================================
-- ABILITY_UNLOCKABLES
-- =========================================
CREATE TABLE ability_unlockables
(
    ability_id    INT NOT NULL,
    unlockable_id INT NOT NULL,
    PRIMARY KEY (ability_id, unlockable_id),
    CONSTRAINT fk_ability_unlockables_ability
        FOREIGN KEY (ability_id) REFERENCES ability (id) ON DELETE CASCADE,
    CONSTRAINT fk_ability_unlockables_unlock
        FOREIGN KEY (unlockable_id) REFERENCES ability (id) ON DELETE CASCADE
);

CREATE INDEX idx_ability_unlockables_ability_id ON ability_unlockables (ability_id);
CREATE INDEX idx_ability_unlockables_unlockables ON ability_unlockables (unlockable_id);

-- =========================================
-- REALMS
-- =========================================
CREATE TABLE realms
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID            NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    realm_name realm_name_enum NOT NULL UNIQUE,
    created_at BIGINT          NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT          NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN         NOT NULL        DEFAULT true
);

CREATE TRIGGER realms_refresh_updated_at
    BEFORE UPDATE
    ON realms
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- MONEY
-- =========================================
CREATE TABLE money
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID            NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    money_name money_name_enum NOT NULL,
    bag_id     INTEGER         NOT NULL REFERENCES bags (id) ON DELETE CASCADE,
    created_at BIGINT          NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT          NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN         NOT NULL        DEFAULT true
);

CREATE INDEX idx_money_bag_id ON money (bag_id);
CREATE TRIGGER money_refresh_updated_at
    BEFORE UPDATE
    ON money
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- REAGENTS
-- =========================================
CREATE TABLE reagents
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID              NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    reagent_name reagent_name_enum NOT NULL,
    bag_id       INTEGER           NOT NULL REFERENCES bags (id) ON DELETE CASCADE,
    created_at   BIGINT            NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT            NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN           NOT NULL        DEFAULT true
);

CREATE INDEX idx_reagents_bag_id ON reagents (bag_id);
CREATE TRIGGER reagents_refresh_updated_at
    BEFORE UPDATE
    ON reagents
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

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

CREATE TRIGGER formations_refresh_updated_at
    BEFORE UPDATE ON formations
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


CREATE TABLE IF NOT EXISTS formation_grants
(
    formation_id INT NOT NULL REFERENCES formations(id) ON DELETE CASCADE,
    ability_id   INT NOT NULL REFERENCES ability(id) ON DELETE CASCADE,
    PRIMARY KEY (formation_id, ability_id)
);

CREATE INDEX IF NOT EXISTS idx_formation_grants_formation_id ON formation_grants(formation_id);
CREATE INDEX IF NOT EXISTS idx_formation_grants_ability_id ON formation_grants(ability_id);

-- =========================================
-- CHARACTERS
-- =========================================
CREATE TABLE characters
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    pg_name     VARCHAR(50),
    campaign_id INTEGER REFERENCES campaigns (id),
    background  TEXT,
    realm_id    INTEGER NOT NULL REFERENCES realms (id),
    user_id     BIGINT  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    card_id     INTEGER UNIQUE REFERENCES cards (id),
    bag_id      INTEGER NOT NULL UNIQUE REFERENCES bags (id),
    formation_id INTEGER REFERENCES formations(id),
    created_at  BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL        DEFAULT true,
    is_playing BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_characters_campaign_id ON characters (campaign_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_one_playing_character_per_user_campaign ON characters(user_id, campaign_id) WHERE is_playing = true;

CREATE INDEX idx_characters_realm_id ON characters (realm_id);
CREATE INDEX idx_characters_user_id ON characters (user_id);
CREATE INDEX idx_characters_card_id ON characters (card_id);
CREATE INDEX idx_characters_bag_id ON characters (bag_id);

CREATE TRIGGER characters_refresh_updated_at
    BEFORE UPDATE
    ON characters
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- CHARACTER_ABILITY (ManyToMany)
-- =========================================
CREATE TABLE character_ability
(
    character_id INT NOT NULL REFERENCES characters (id) ON DELETE CASCADE,
    ability_id   INT NOT NULL REFERENCES ability (id) ON DELETE CASCADE,
    PRIMARY KEY (character_id, ability_id)
);

CREATE INDEX idx_character_ability_character_id ON character_ability (character_id);
CREATE INDEX idx_character_ability_ability_id ON character_ability (ability_id);

-- =========================================
-- DIARIES
-- =========================================
CREATE TABLE diaries
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID         NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    owner_id   INTEGER      NOT NULL REFERENCES characters (id) ON DELETE CASCADE,
    created_at BIGINT       NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT       NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN      NOT NULL        DEFAULT true
);

CREATE INDEX idx_diaries_owner_id ON diaries (owner_id);

CREATE TRIGGER diaries_refresh_updated_at
    BEFORE UPDATE
    ON diaries
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- DIARY_ENTRY
-- =========================================
CREATE TABLE diary_entry
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    diary_id    INTEGER NOT NULL REFERENCES diaries (id) ON DELETE CASCADE,
    date        BIGINT,
    description TEXT,
    created_at  BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL        DEFAULT true
);

CREATE INDEX idx_diary_entry_diary_id ON diary_entry (diary_id);

CREATE TRIGGER diary_entry_refresh_updated_at
    BEFORE UPDATE
    ON diary_entry
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- PARAGRAPHS
-- =========================================
CREATE TABLE paragraphs
(
    id             SERIAL PRIMARY KEY,
    uuid           UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    diary_entry_id INTEGER NOT NULL REFERENCES diary_entry (id) ON DELETE CASCADE,
    date           BIGINT,
    description    TEXT,
    created_at     BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at     BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active      BOOLEAN NOT NULL        DEFAULT true
);

CREATE INDEX idx_paragraphs_diary_entry_id ON paragraphs (diary_entry_id);

CREATE TRIGGER paragraphs_refresh_updated_at
    BEFORE UPDATE
    ON paragraphs
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- DIARY_IMAGE
-- =========================================
CREATE TABLE diary_image
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID    NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    image_data BYTEA   NOT NULL,
    created_at BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT  NOT NULL        DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL        DEFAULT true
);

CREATE TRIGGER diary_image_refresh_updated_at
    BEFORE UPDATE
    ON diary_image
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- DIARY_IMAGE ↔ PARAGRAPH (ManyToMany)
-- =========================================
CREATE TABLE diary_image_paragraph
(
    diary_image_id INTEGER NOT NULL REFERENCES diary_image (id) ON DELETE CASCADE,
    paragraph_id   INTEGER NOT NULL REFERENCES paragraphs (id) ON DELETE CASCADE,
    PRIMARY KEY (diary_image_id, paragraph_id)
);

CREATE INDEX idx_dip_image_id ON diary_image_paragraph (diary_image_id);
CREATE INDEX idx_dip_paragraph_id ON diary_image_paragraph (paragraph_id);

CREATE TYPE submission_status_enum AS ENUM ('draft', 'submitted', 'approved', 'rejected');

CREATE TABLE IF NOT EXISTS character_submissions
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    character_id INTEGER NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    version      INTEGER NOT NULL,
    background   TEXT,
    status       submission_status_enum NOT NULL DEFAULT 'draft',
    submitted_at BIGINT,
    created_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT ux_character_submission_version UNIQUE(character_id, version)
);

CREATE INDEX IF NOT EXISTS idx_character_submissions_character_id ON character_submissions(character_id);
CREATE INDEX IF NOT EXISTS idx_character_submissions_status ON character_submissions(status);

CREATE TRIGGER character_submissions_refresh_updated_at
    BEFORE UPDATE ON character_submissions
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();


CREATE TABLE IF NOT EXISTS submission_reviews
(
    id            SERIAL PRIMARY KEY,
    uuid          UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    submission_id INTEGER NOT NULL REFERENCES character_submissions(id) ON DELETE CASCADE,
    reviewer_id   INTEGER NOT NULL REFERENCES users(id),
    status        submission_status_enum NOT NULL,
    note          TEXT,
    created_at    BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at    BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active     BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX IF NOT EXISTS idx_submission_reviews_submission_id ON submission_reviews(submission_id);
CREATE INDEX IF NOT EXISTS idx_submission_reviews_reviewer_id ON submission_reviews(reviewer_id);

CREATE TRIGGER submission_reviews_refresh_updated_at
    BEFORE UPDATE ON submission_reviews
    FOR EACH ROW EXECUTE FUNCTION refresh_updated_at();
