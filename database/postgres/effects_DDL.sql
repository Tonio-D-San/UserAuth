CREATE TABLE effects
(
    prefix VARCHAR(255) NOT NULL,
    call   VARCHAR(255) NOT NULL,
    uuid   UUID         NOT NULL UNIQUE,
    PRIMARY KEY (prefix, call)
);
CREATE TABLE calls
(
    name        VARCHAR(255) NOT NULL PRIMARY KEY,
    description TEXT,
    duration    BIGINT
);
