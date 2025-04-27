CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID         NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    surname     VARCHAR(50)  NOT NULL,
    biography   TEXT,
    email       VARCHAR(100) NOT NULL UNIQUE,
    is_active   BOOLEAN      NOT NULL,
    img_profile BYTEA
);

CREATE TABLE groups
(
    id          SERIAL PRIMARY KEY,
    uuid        UUID        NOT NULL UNIQUE,
    name        VARCHAR(50) NOT NULL,
    path        VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE user_group
(
    user_id SERIAL REFERENCES users (id),
    group_id  SERIAL REFERENCES groups (id),
    PRIMARY KEY (user_id, group_id)
);

INSERT INTO users (uuid, name, surname, biography, email, is_active, img_profile)
VALUES ('167fc152-213f-4c2d-b2f9-03a3ddce4a4c', 'John', 'Doe', '',
        'johndoe@test.com', TRUE, NULL);

INSERT INTO groups (uuid, name, path, description)
VALUES ('7de6d481-9da9-4bcc-aca2-30073030ae9d',
        'users',
        '/users',
        'Role for admin'),
       ('f70783d9-b64b-46b9-81c2-e4af0e5f9889',
        'admin-service-administrators',
        '/users/admin-service-administrators',
        'Role for admin'),
       ('e246270c-acaa-4aed-8d9f-4b7bbe95f2cb',
        'user-service-administrators',
        '/users/user-service-administrators',
        'Role for users');

INSERT INTO user_group(user_id, group_id)
VALUES (1, 2),
       (1, 3)