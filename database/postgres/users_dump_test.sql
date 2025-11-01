INSERT INTO users (uuid, provider, provider_id, name, surname, biography, email, is_active, img_profile)
VALUES  (gen_random_uuid(), 'Google', 'googleID', 'John', 'Doe', '','johndoe@test.com', TRUE, NULL),
        (gen_random_uuid(), 'Google', 'googleID', 'Marco', 'Carta', '', 'mcarta@test.com', TRUE, NULL),
        (gen_random_uuid(), 'Google', 'googleID', 'Tiziano', 'Ferro', '', 'tferro@test.com', TRUE, NULL),
        (gen_random_uuid(), 'Google', 'googleID', 'Giovanni', 'Sasso', '', 'gsasso@test.com', TRUE,NULL),
        (gen_random_uuid(), 'Google', 'googleID', 'Pippo', 'Baudo', '', 'pbaudo@test.com', FALSE, NULL),
        (gen_random_uuid(), 'Google', 'googleID', 'Leonida', 'Forbici', '', 'leforbici@test.com', TRUE, NULL),
        (gen_random_uuid(), 'Google', 'googleID', 'Francesco', 'Primo', '', 'fprimo@test.com', TRUE,NULL);

INSERT INTO user_group (user_id, group_id)
VALUES  (1, 3),
        (1, 4),
        (2, 4),
        (3, 4),
        (4, 4),
        (5, 4),
        (6, 4),
        (7, 1);