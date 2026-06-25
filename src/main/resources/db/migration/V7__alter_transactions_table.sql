ALTER table transactions alter column amount type numeric(19,2) using amount::numeric(19,2);
ALTER table transactions alter payment_mode type varchar(255) using payment_mode::varchar(255);
ALTER table transactions alter transaction_type type varchar(255) using transaction_type::varchar(255);
ALTER table transactions alter transaction_status type varchar(255) using transaction_status::varchar(255);