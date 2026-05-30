CREATE TABLE transactions(
    transaction_id bigserial PRIMARY KEY,
    amount bigint not null,
    sender_wallet_id bigint,
    receiver_wallet_id bigint,
    payment_mode int,
    external_reference varchar(255),
    reference_transaction_id bigint,
    transaction_type int not null,
    transaction_status int not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    idempotency_key varchar(255) UNIQUE,
    CONSTRAINT chk_wallet_presence CHECK (sender_wallet_id IS NOT NULL OR receiver_wallet_id IS NOT NULL),
    CONSTRAINT fk_sender_wallet FOREIGN KEY (sender_wallet_id) REFERENCES wallets(wallet_id),
    CONSTRAINT fk_receiver_wallet FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(wallet_id)
)