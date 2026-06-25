ALTER table wallets rename column balance to available_balance;
ALTER table wallets add column reserved_balance numeric(19, 2) default 0 not null;
ALTER table wallets add column total_balance numeric(19, 2) default 0 not null;