CREATE TABLE effects (
    prefix VARCHAR(255) NOT NULL,
    call VARCHAR(255) NOT NULL,
    uuid UUID NOT NULL UNIQUE,
    duration BIGINT,
    PRIMARY KEY (prefix, call)
);
