INSERT INTO users (uuid, name, surname, biography, email, is_active, img_profile)
VALUES ('5a265f75-597d-4fd8-958b-1d02fbf7b0e8', 'Marco', 'Carta', '', 'mcarta@test.com', TRUE, NULL),
       ('bd4e3b0b-6dc1-4006-b512-d0d4407ec08a', 'Tiziano', 'Ferro', '', 'tferro@test.com', TRUE, NULL),
       ('90e8ddfc-6fbe-4107-bc02-7ef38c0a8d0e', 'Giovanni', 'Sasso', '', 'gsasso@test.com', TRUE,NULL),
       ('c456db89-a8af-4a94-afee-03bb5a0dff6a', 'Pippo', 'Baudo', '', 'pbaudo@test.com', FALSE, NULL),
       ('b5aa3745-15d7-4e24-bc17-f71ad9b1cfed', 'Leonida', 'Forbici', '', 'leforbici@test.com', TRUE, NULL),
       ('b9afa0e2-1ca7-4b3e-912b-1574d87c085b', 'Francesco', 'Primo', '', 'fprimo@test.com', TRUE,NULL);

INSERT INTO user_group (user_id, group_id)
VALUES (2, 3),
       (3, 3),
       (4, 3),
       (5, 3),
       (6, 3),
       (7, 3);