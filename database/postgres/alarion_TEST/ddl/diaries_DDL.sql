-- =========================================
-- DIARIES
-- =========================================
CREATE TABLE diaries
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    owner_id   BIGSERIAL NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_diaries_owner_id ON diaries(owner_id);

CREATE TRIGGER diaries_refresh_updated_at
    BEFORE UPDATE ON diaries
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- DIARY_ENTRY
-- =========================================
CREATE TABLE diary_entry
(
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    diary_id    BIGSERIAL NOT NULL REFERENCES diaries(id) ON DELETE CASCADE,
    date        BIGINT,
    description TEXT,
    created_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at  BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active   BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_diary_entry_diary_id ON diary_entry(diary_id);

CREATE TRIGGER diary_entry_refresh_updated_at
    BEFORE UPDATE ON diary_entry
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- PARAGRAPHS
-- =========================================
CREATE TABLE paragraphs
(
    id             BIGSERIAL PRIMARY KEY,
    uuid           UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    diary_entry_id BIGSERIAL NOT NULL REFERENCES diary_entry(id) ON DELETE CASCADE,
    date           BIGINT,
    description    TEXT,
    created_at     BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at     BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active      BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_paragraphs_diary_entry_id ON paragraphs(diary_entry_id);

CREATE TRIGGER paragraphs_refresh_updated_at
    BEFORE UPDATE ON paragraphs
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- DIARY_IMAGE
-- =========================================
CREATE TABLE diary_image
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    image_data BYTEA NOT NULL,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_active  BOOLEAN NOT NULL DEFAULT true
);

CREATE TRIGGER diary_image_refresh_updated_at
    BEFORE UPDATE ON diary_image
    FOR EACH ROW
EXECUTE FUNCTION refresh_updated_at();

-- =========================================
-- DIARY_IMAGE ↔ PARAGRAPH (ManyToMany)
-- =========================================
CREATE TABLE diary_image_paragraph
(
    diary_image_id BIGSERIAL NOT NULL REFERENCES diary_image(id) ON DELETE CASCADE,
    paragraph_id   BIGSERIAL NOT NULL REFERENCES paragraphs(id) ON DELETE CASCADE,
    PRIMARY KEY (diary_image_id, paragraph_id)
);

CREATE INDEX idx_dip_image_id ON diary_image_paragraph(diary_image_id);
CREATE INDEX idx_dip_paragraph_id ON diary_image_paragraph(paragraph_id);
