CREATE TABLE users
(
    id         UUID         NOT NULL,
    email      VARCHAR(255) UNIQUE,
    password   VARCHAR(255),
    nickname   VARCHAR(255) NOT NULL UNIQUE,
    provider   VARCHAR(20)  NOT NULL,
    provider_id VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);
