-- =========================
-- RULESET / CATALOGO
-- =========================

CREATE TABLE IF NOT EXISTS rulesets
(
    id                     BIGSERIAL PRIMARY KEY,
    uuid                   UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at             BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at             BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active              BOOLEAN     NOT NULL DEFAULT TRUE,

    name                   TEXT        NOT NULL,
    description            TEXT        NULL,

    initial_points         INTEGER     NOT NULL DEFAULT 0,
    required_spend_points  INTEGER     NOT NULL DEFAULT 0,
    max_points_at_creation INTEGER     NULL,

    CONSTRAINT uq_rulesets_uuid UNIQUE (uuid),
    CONSTRAINT uq_rulesets_name UNIQUE (name),
    CONSTRAINT ck_rulesets_points_nonneg CHECK (
        initial_points >= 0 AND required_spend_points >= 0 AND
        (max_points_at_creation IS NULL OR max_points_at_creation >= 0)
        )
);

CREATE TABLE IF NOT EXISTS realms
(
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,

    ruleset_id  BIGINT      NOT NULL,
    name        TEXT        NOT NULL,
    description TEXT        NULL,

    CONSTRAINT uq_realms_uuid UNIQUE (uuid),
    CONSTRAINT uq_realms_ruleset_name UNIQUE (ruleset_id, name),

    CONSTRAINT fk_realms_ruleset
        FOREIGN KEY (ruleset_id) REFERENCES rulesets(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS abilities
(
    id            BIGSERIAL PRIMARY KEY,
    uuid          UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at    BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at    BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active     BOOLEAN     NOT NULL DEFAULT TRUE,

    ruleset_id    BIGINT      NOT NULL,

    code          TEXT        NOT NULL,
    name          TEXT        NOT NULL,
    description   TEXT        NULL,

    is_repeatable BOOLEAN     NOT NULL DEFAULT FALSE,
    max_rank      INTEGER     NULL,

    CONSTRAINT uq_abilities_uuid UNIQUE (uuid),
    CONSTRAINT uq_abilities_ruleset_code UNIQUE (ruleset_id, code),
    CONSTRAINT ck_abilities_rank CHECK (
        (is_repeatable = FALSE AND max_rank IS NULL)
            OR
        (is_repeatable = TRUE AND max_rank IS NOT NULL AND max_rank >= 1)
        ),

    CONSTRAINT fk_abilities_ruleset
        FOREIGN KEY (ruleset_id) REFERENCES rulesets(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS ability_costs
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN     NOT NULL DEFAULT TRUE,

    ability_id BIGINT      NOT NULL,
    rank       INTEGER     NOT NULL DEFAULT 1,
    cost       INTEGER     NOT NULL DEFAULT 0,

    CONSTRAINT uq_ability_costs_uuid UNIQUE (uuid),
    CONSTRAINT uq_ability_costs_ability_rank UNIQUE (ability_id, rank),
    CONSTRAINT ck_ability_costs CHECK (rank >= 1 AND cost >= 0),

    CONSTRAINT fk_ability_costs_ability
        FOREIGN KEY (ability_id) REFERENCES abilities(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ability_prerequisites
(
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at          BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at          BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,

    ability_id          BIGINT      NOT NULL,
    required_ability_id BIGINT      NOT NULL,
    required_rank       INTEGER     NOT NULL DEFAULT 1,

    CONSTRAINT uq_ability_prereq_uuid UNIQUE (uuid),
    CONSTRAINT uq_ability_prereq UNIQUE (ability_id, required_ability_id),
    CONSTRAINT ck_ability_prereq_rank CHECK (required_rank >= 1),
    CONSTRAINT ck_ability_prereq_not_self CHECK (ability_id <> required_ability_id),

    CONSTRAINT fk_ability_prereq_ability
        FOREIGN KEY (ability_id) REFERENCES abilities(id) ON DELETE CASCADE,
    CONSTRAINT fk_ability_prereq_required
        FOREIGN KEY (required_ability_id) REFERENCES abilities(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS trainings
(
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,

    ruleset_id  BIGINT      NOT NULL,
    name        TEXT        NOT NULL,
    description TEXT        NULL,

    CONSTRAINT uq_trainings_uuid UNIQUE (uuid),
    CONSTRAINT uq_trainings_ruleset_name UNIQUE (ruleset_id, name),

    CONSTRAINT fk_trainings_ruleset
        FOREIGN KEY (ruleset_id) REFERENCES rulesets(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS training_ability_grants
(
    id           BIGSERIAL PRIMARY KEY,
    uuid         UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,

    training_id  BIGINT      NOT NULL,
    ability_id   BIGINT      NOT NULL,
    rank_granted INTEGER     NOT NULL DEFAULT 1,

    CONSTRAINT uq_training_grants_uuid UNIQUE (uuid),
    CONSTRAINT uq_training_grants UNIQUE (training_id, ability_id),
    CONSTRAINT ck_training_grants_rank CHECK (rank_granted >= 1),

    CONSTRAINT fk_training_grants_training
        FOREIGN KEY (training_id) REFERENCES trainings(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_grants_ability
        FOREIGN KEY (ability_id) REFERENCES abilities(id) ON DELETE RESTRICT
);

-- =========================
-- PERSONAGGIO
-- =========================

CREATE TABLE IF NOT EXISTS characters
(
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,

    ruleset_id  BIGINT      NOT NULL,
    realm_id    BIGINT      NOT NULL,
    training_id BIGINT      NULL,

    user_id     BIGINT      NOT NULL,

    pg_name     TEXT        NOT NULL,
    background  TEXT        NULL,

    CONSTRAINT uq_characters_uuid UNIQUE (uuid),

    CONSTRAINT fk_characters_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_characters_ruleset
        FOREIGN KEY (ruleset_id) REFERENCES rulesets(id) ON DELETE RESTRICT,
    CONSTRAINT fk_characters_realm
        FOREIGN KEY (realm_id) REFERENCES realms(id) ON DELETE RESTRICT,
    CONSTRAINT fk_characters_training
        FOREIGN KEY (training_id) REFERENCES trainings(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS acquisition_sources
(
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,

    code        TEXT        NOT NULL,
    name        TEXT        NOT NULL,
    description TEXT        NULL,

    CONSTRAINT uq_sources_uuid UNIQUE (uuid),
    CONSTRAINT uq_sources_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS character_abilities
(
    id           BIGSERIAL PRIMARY KEY,
    uuid         UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,

    character_id BIGINT      NOT NULL,
    ability_id   BIGINT      NOT NULL,
    rank         INTEGER     NOT NULL DEFAULT 1,

    source_id    BIGINT      NOT NULL,
    cost_paid    INTEGER     NOT NULL DEFAULT 0,

    CONSTRAINT uq_character_abilities_uuid UNIQUE (uuid),
    CONSTRAINT uq_character_abilities_unique UNIQUE (character_id, ability_id, rank),
    CONSTRAINT ck_character_abilities_rank CHECK (rank >= 1),
    CONSTRAINT ck_character_abilities_cost CHECK (cost_paid >= 0),

    CONSTRAINT fk_char_abilities_character
        FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    CONSTRAINT fk_char_abilities_ability
        FOREIGN KEY (ability_id) REFERENCES abilities(id) ON DELETE RESTRICT,
    CONSTRAINT fk_char_abilities_source
        FOREIGN KEY (source_id) REFERENCES acquisition_sources(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS point_transactions
(
    id           BIGSERIAL PRIMARY KEY,
    uuid         UUID        NOT NULL DEFAULT gen_random_uuid(),
    created_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at   BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,

    character_id BIGINT      NOT NULL,

    delta        INTEGER     NOT NULL,
    reason       TEXT        NULL,

    ref_type     TEXT        NULL,
    ref_uuid     UUID        NULL,

    CONSTRAINT uq_point_tx_uuid UNIQUE (uuid),

    CONSTRAINT fk_point_tx_character
        FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE
);

-- =========================
-- INDICI UTILI
-- =========================

CREATE INDEX IF NOT EXISTS ix_realms_ruleset_id ON realms (ruleset_id);
CREATE INDEX IF NOT EXISTS ix_abilities_ruleset_id ON abilities (ruleset_id);
CREATE INDEX IF NOT EXISTS ix_ability_costs_ability_id ON ability_costs (ability_id);
CREATE INDEX IF NOT EXISTS ix_prereq_ability_id ON ability_prerequisites (ability_id);
CREATE INDEX IF NOT EXISTS ix_trainings_ruleset_id ON trainings (ruleset_id);
CREATE INDEX IF NOT EXISTS ix_training_grants_training_id ON training_ability_grants (training_id);

CREATE INDEX IF NOT EXISTS ix_characters_user_id ON characters (user_id);
CREATE INDEX IF NOT EXISTS ix_characters_ruleset_id ON characters (ruleset_id);
CREATE INDEX IF NOT EXISTS ix_char_abilities_character_id ON character_abilities (character_id);
CREATE INDEX IF NOT EXISTS ix_point_tx_character_id ON point_transactions (character_id);
CREATE INDEX IF NOT EXISTS ix_point_tx_ref ON point_transactions (ref_type, ref_uuid);
