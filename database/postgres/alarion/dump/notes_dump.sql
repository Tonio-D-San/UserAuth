-- ===========================================
-- DUMP ENUM AbilityName → ability
-- ===========================================

INSERT INTO notes (uuid, note)
VALUES (gen_random_uuid(), 'marshall.note'),
       (gen_random_uuid(), 'tenacity.note_one'),
       (gen_random_uuid(), 'tenacity.note_two'),
       (gen_random_uuid(), 'toxicologist.note')
;

-- ==============================
-- RELAZIONI (PREREQUISITI)
-- ==============================
-- RELAZIONI (NOTE)
INSERT INTO ability_notes (ability_id, note_id)
VALUES
    (
        (SELECT id FROM ability WHERE code = 'MARSHAL'),
        (SELECT id FROM notes WHERE note = 'marshall.note')
    ),
    (
        (SELECT id FROM ability WHERE code = 'TENACITY'),
        (SELECT id FROM notes WHERE note = 'tenacity.note_one')
    ),
    (
        (SELECT id FROM ability WHERE code = 'TENACITY'),
        (SELECT id FROM notes WHERE note = 'tenacity.note_two')
    ),
    (
        (SELECT id FROM ability WHERE code = 'TOXICOLOGIST'),
        (SELECT id FROM notes WHERE note = 'toxicologist.note')
    );
