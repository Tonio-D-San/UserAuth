CREATE OR REPLACE FUNCTION refresh_last_access()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.last_access = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    uuid         UUID                      NOT NULL UNIQUE,
    provider     VARCHAR(50)               NOT NULL,
    provider_id  VARCHAR(50)               NOT NULL,
    name         VARCHAR(100)              NOT NULL,
    surname      VARCHAR(50)               NOT NULL,
    biography    TEXT,
    first_access timestamptz DEFAULT now() NOT NULL,
    last_access  timestamptz DEFAULT now() NOT NULL,
    email        VARCHAR(100)              NOT NULL UNIQUE,
    password     VARCHAR(100),
    is_active    BOOLEAN                   NOT NULL,
    img_profile  BYTEA
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
    user_id  SERIAL REFERENCES users (id),
    group_id SERIAL REFERENCES groups (id),
    PRIMARY KEY (user_id, group_id)
);

CREATE TRIGGER users_refresh_last_access
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION refresh_last_access();
