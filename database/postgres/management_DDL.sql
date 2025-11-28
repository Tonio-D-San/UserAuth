CREATE TABLE orders
(
    id   VARCHAR(32) PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE
);
CREATE TABLE calls
(
    name        VARCHAR(255) NOT NULL PRIMARY KEY,
    description TEXT,
    duration    BIGINT
);
