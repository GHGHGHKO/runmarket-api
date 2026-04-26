CREATE TABLE race_likes
(
    id         UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    race_id    UUID      NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_race_likes PRIMARY KEY (id),
    CONSTRAINT uq_race_likes_user_race UNIQUE (user_id, race_id),
    CONSTRAINT fk_race_likes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_race_likes_race FOREIGN KEY (race_id) REFERENCES races (id) ON DELETE CASCADE
);
