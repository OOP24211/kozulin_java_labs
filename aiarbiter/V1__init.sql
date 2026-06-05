CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE chat_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    worker_a_name VARCHAR(100),
    worker_a_response TEXT,
    worker_a_tokens INT,
    worker_b_name VARCHAR(100),
    worker_b_response TEXT,
    worker_b_tokens INT,
    verdict TEXT,
    winner CHAR(1),
    latency_ms INT,
    created_at TIMESTAMP DEFAULT NOW()
);
