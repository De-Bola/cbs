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
    PRIMARY KEY (balance_id)
);
-- little changes to data types here

-- Table: transactions
DROP TABLE IF EXISTS transactions CASCADE;
CREATE TABLE IF NOT EXISTS transactions
(
    amount numeric(38,2),
    trx_id bigint NOT NULL,
    currency VARCHAR (255),
    trx_type VARCHAR (255),
    description VARCHAR (255),
    balanceAfterTrx numeric(38,2),
    account_id uuid NOT NULL,
    PRIMARY KEY (trx_id)
);
-- no need to add 'balance' item to trx table.
-- Just write an update query using account_id as key and where currency = currency.