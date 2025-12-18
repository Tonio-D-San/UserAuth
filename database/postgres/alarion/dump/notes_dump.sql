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
INSERT INTO ability_notes (ability_id, note_id)
VALUES ((SELECT id FROM ability WHERE code = 'MARSHAL'), (SELECT id FROM ability WHERE code = 1)),
       ((SELECT id FROM ability WHERE code = 'TENACITY'), (SELECT id FROM ability WHERE code = 2)),
       ((SELECT id FROM ability WHERE code = 'TENACITY'), (SELECT id FROM ability WHERE code =  3)),
       ((SELECT id FROM ability WHERE code = 'TOXICOLOGIST'), (SELECT id FROM ability WHERE code = 4))
;