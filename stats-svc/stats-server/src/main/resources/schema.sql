CREATE TABLE IF NOT EXISTS endpoint_hits
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY,
    app       VARCHAR(50)  NOT NULL,
    uri       VARCHAR(100) NOT NULL,
    ip        VARCHAR(20)  NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT endpoint_hit_pk
        PRIMARY KEY (id)
);