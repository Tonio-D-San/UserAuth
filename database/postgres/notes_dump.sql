-- ===========================================
-- DUMP ENUM AbilityName → ability_definition
-- ===========================================

INSERT INTO notes (uuid, note)
VALUES (gen_random_uuid(), 'marshall.note'),
       (gen_random_uuid(), 'tenacity.note_one'),
       (gen_random_uuid(), 'tenacity.note_two')
;

-- ==============================
-- RELAZIONI (PREREQUISITI)
-- ==============================
INSERT INTO ability_definition_notes (ability_definition_code, note_id)
VALUES ('MARSHAL', 1),
       ('TENACITY', 2),
       ('TENACITY', 3)
;