CREATE TABLE roles
(
    id         UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    role_type  VARCHAR(20) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT fk_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_roles_user_id ON roles (user_id);
