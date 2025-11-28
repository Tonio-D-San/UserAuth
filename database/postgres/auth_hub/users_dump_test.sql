INSERT INTO users (uuid, provider, provider_id, name, surname, biography, email, password, is_active, img_profile)
VALUES  ('167fc152-213f-4c2d-b2f9-03a3ddce4a4c', 'Google', 'googleID', 'John', 'Doe', '','johndoe@test.com', 'password', TRUE, NULL),
        ('5a265f75-597d-4fd8-958b-1d02fbf7b0e8', 'Google', 'googleID', 'Marco', 'Carta', '', 'mcarta@test.com', 'password', TRUE, NULL),
        ('bd4e3b0b-6dc1-4006-b512-d0d4407ec08a', 'Google', 'googleID', 'Tiziano', 'Ferro', '', 'tferro@test.com', 'password', TRUE, NULL),
        ('90e8ddfc-6fbe-4107-bc02-7ef38c0a8d0e', 'Google', 'googleID', 'Giovanni', 'Sasso', '', 'gsasso@test.com', 'password', TRUE,NULL),
        ('c456db89-a8af-4a94-afee-03bb5a0dff6a', 'Google', 'googleID', 'Pippo', 'Baudo', '', 'pbaudo@test.com', 'password', FALSE, NULL),
        ('b5aa3745-15d7-4e24-bc17-f71ad9b1cfed', 'Google', 'googleID', 'Leonida', 'Forbici', '', 'leforbici@test.com', 'password', TRUE, NULL),
        ('b9afa0e2-1ca7-4b3e-912b-1574d87c085b', 'Google', 'googleID', 'Francesco', 'Primo', '', 'fprimo@test.com', 'password', TRUE,NULL);

INSERT INTO user_group (user_id, group_id)
VALUES  (1, 3),
        (1, 4),
        (2, 4),
        (3, 4),
        (4, 4),
        (5, 4),
        (6, 4),
        (7, 1);
;