-- =========================================
-- ENUM PLACEHOLDERS (se li vuoi come enum DB)
-- =========================================
-- CREATE TYPE money_name AS ENUM ('cartographer', 'constitution');
-- CREATE TYPE ability_name AS ENUM (...);
-- CREATE TYPE reagent_name AS ENUM (...);
-- CREATE TYPE kingdom_name AS ENUM (...);

-- =========================================
-- BAG
-- =========================================
CREATE TABLE bags
(
    id   SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE
);

-- =========================================
-- CARD
-- =========================================
CREATE TABLE cards
(
    id               SERIAL PRIMARY KEY,
    uuid             UUID NOT NULL UNIQUE,
    total_points     INTEGER,
    available_points INTEGER,
    used_points      INTEGER
);

-- =========================================
-- player_abilities
-- =========================================
CREATE TABLE player_abilities
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID NOT NULL UNIQUE,
    ability_name VARCHAR(50),
    background   TEXT
);

-- =========================================
-- players
-- =========================================
CREATE TABLE players
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID    NOT NULL UNIQUE,
    pg_name    VARCHAR(50),
    background TEXT,
    user_id    BIGINT  NOT NULL,
    card_id    INTEGER UNIQUE,
    bag_id     INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_player_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_player_card FOREIGN KEY (card_id) REFERENCES cards (id),
    CONSTRAINT fk_player_bag FOREIGN KEY (bag_id) REFERENCES bags (id)
);

-- =========================================
-- DIARY
-- =========================================
CREATE TABLE diaries
(
    id       SERIAL PRIMARY KEY,
    uuid     UUID         NOT NULL UNIQUE,
    name     VARCHAR(100) NOT NULL UNIQUE,
    owner_id INTEGER      NOT NULL,
    CONSTRAINT fk_diary_owner FOREIGN KEY (owner_id) REFERENCES players (id) ON DELETE CASCADE
);

-- =========================================
-- DIARY_ENTRY
-- =========================================
CREATE TABLE diary_entry
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID    NOT NULL UNIQUE,
    diary_id    INTEGER NOT NULL REFERENCES diaries (id) ON DELETE CASCADE,
    date        BIGINT,
    description TEXT
);

-- =========================================
-- PARAGRAPH
-- =========================================
CREATE TABLE paragraphs
(
    id             SERIAL PRIMARY KEY,
    uuid           UUID    NOT NULL UNIQUE,
    diary_entry_id INTEGER NOT NULL REFERENCES diary_entry (id) ON DELETE CASCADE,
    date           BIGINT,
    description    TEXT
);

-- =========================================
-- DIARY_IMAGE
-- =========================================
CREATE TABLE diary_image
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID  NOT NULL UNIQUE,
    image_data BYTEA NOT NULL
);


-- =========================================
-- DIARY_IMAGE ↔ PARAGRAPH (ManyToMany)
-- =========================================
CREATE TABLE diary_image_paragraph
(
    diary_image_id INTEGER NOT NULL REFERENCES diary_image (id) ON DELETE CASCADE,
    paragraph_id   INTEGER NOT NULL REFERENCES paragraphs (id) ON DELETE CASCADE,
    PRIMARY KEY (diary_image_id, paragraph_id)
);

-- =========================================
-- MONEY
-- =========================================
CREATE TABLE money
(
    id         SERIAL PRIMARY KEY,
    uuid       UUID    NOT NULL UNIQUE,
    money_name VARCHAR(50),
    bag_id     INTEGER NOT NULL,
    CONSTRAINT fk_money_bag FOREIGN KEY (bag_id) REFERENCES bags (id) ON DELETE CASCADE
);

-- =========================================
-- reagents
-- =========================================
CREATE TABLE reagents
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID    NOT NULL UNIQUE,
    reagent_name VARCHAR(50),
    bag_id       INTEGER NOT NULL,
    CONSTRAINT fk_reagent_bag FOREIGN KEY (bag_id) REFERENCES bags (id) ON DELETE CASCADE
);

-- =========================================
-- KINGDOM
-- =========================================
CREATE TABLE kingdoms
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID NOT NULL UNIQUE,
    kingdom_name VARCHAR(50),
    card_id      INTEGER UNIQUE,
    player_id    INTEGER UNIQUE,
    CONSTRAINT fk_kingdom_card FOREIGN KEY (card_id) REFERENCES cards (id),
    CONSTRAINT fk_kingdom_player FOREIGN KEY (player_id) REFERENCES players (id) ON DELETE CASCADE
);


-- =========================================
-- PLAYER_ABILITY (join table ManyToMany)
-- =========================================
CREATE TABLE player_ability
(
    player_id  INTEGER NOT NULL REFERENCES players (id) ON DELETE CASCADE,
    ability_id INTEGER NOT NULL REFERENCES player_abilities (id) ON DELETE CASCADE,
    PRIMARY KEY (player_id, ability_id)
);
