CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notes_user_id ON notes(user_id);

-- demo / demo123 (BCrypt)
INSERT INTO users (username, password_hash, enabled)
VALUES ('demo', '$2y$12$UI1/4VUHeuvqlqrLESkCy.wfP4dgHIx5REO5P/ETSYTrliWOCno4q', TRUE)
ON CONFLICT (username) DO NOTHING;
