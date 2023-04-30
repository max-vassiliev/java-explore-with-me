CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name  VARCHAR(50)  NOT NULL,
    email VARCHAR(100) NOT NULL,
    CONSTRAINT users_pk
        PRIMARY KEY (id),
    CONSTRAINT uq_email
        UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(20) NOT NULL,
    CONSTRAINT categories_pk
        PRIMARY KEY (id),
    CONSTRAINT uq_name
        UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    CONSTRAINT locations_pk
        PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY,
    title              VARCHAR(120)  NOT NULL,
    annotation         VARCHAR(2000) NOT NULL,
    category_id        BIGINT        NOT NULL,
    paid               BOOLEAN       NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT        NOT NULL,
    description        VARCHAR(7000),
    participant_limit  INTEGER       NOT NULL,
    state              VARCHAR(20)   NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    location_id        BIGINT        NOT NULL,
    request_moderation BOOLEAN       NOT NULL,
    confirmed_requests BIGINT        NOT NULL,
    views              BIGINT        NOT NULL,
    CONSTRAINT events_pk
        PRIMARY KEY (id),
    CONSTRAINT events_categories_fk
        FOREIGN KEY (category_id) REFERENCES categories
            ON DELETE RESTRICT,
    CONSTRAINT events_users_fk
        FOREIGN KEY (initiator_id)
            REFERENCES users,
    CONSTRAINT events_locations_fk
        FOREIGN KEY (location_id) REFERENCES locations
            ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY,
    event_id     BIGINT      NOT NULL,
    requester_id BIGINT      NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR(15) NOT NULL,
    CONSTRAINT requests_pk
        PRIMARY KEY (id),
    CONSTRAINT requests_events_null_fk
        FOREIGN KEY (event_id) REFERENCES events
            ON DELETE CASCADE,
    CONSTRAINT requests_users_null_fk
        FOREIGN KEY (requester_id) REFERENCES users
            ON DELETE CASCADE,
    CONSTRAINT unique_request
        UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY,
    title  VARCHAR(100) NOT NULL,
    pinned BOOLEAN      NOT NULL,
    CONSTRAINT compilations_pk
        PRIMARY KEY (id),
    CONSTRAINT uq_title
        UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT events_compilations_pk
        PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT events_compilations_events_fk
        FOREIGN KEY (event_id) REFERENCES events
            ON DELETE CASCADE,
    CONSTRAINT events_compilations_compilations_fk
        FOREIGN KEY (compilation_id) REFERENCES compilations
            ON DELETE CASCADE
);