CREATE TABLE wallets (
    wallet_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL UNIQUE,
    balance bigint NOT NULL default 0,
    status int not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    version int default 0,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id)
)