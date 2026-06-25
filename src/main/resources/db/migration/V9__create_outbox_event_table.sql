CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE outbox_event (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id BIGINT NOT NULL,
    event_type VARCHAR(255),
    payload TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_outbox_event_transaction_id ON outbox_event (transaction_id);
CREATE INDEX idx_outbox_event_status ON outbox_event (status);