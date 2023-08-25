-- Create tables
-- Table: accounts
DROP TABLE IF EXISTS accounts CASCADE;
CREATE TABLE IF NOT EXISTS accounts
(
    account_id uuid NOT NULL ,
    customer_id VARCHAR (255),
    country VARCHAR (255),
    PRIMARY KEY (account_id)
);

-- Table: balances
DROP TABLE IF EXISTS balances CASCADE;
CREATE TABLE IF NOT EXISTS balances
(
    amount numeric(38,2),
    balance_id bigint NOT NULL,
    currency VARCHAR (255),
    account_id uuid NOT NULL,
    PRIMARY KEY (balance_id),
    CONSTRAINT FK_account_balance FOREIGN KEY (account_id) REFERENCES accounts (account_id)
);
-- little changes to datatypes here