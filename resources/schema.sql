-- Schema pentru BankingApp (idempotent)
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS clients;

CREATE TABLE clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    cnp TEXT UNIQUE,
    phone_number TEXT,
    client_code TEXT,
    onboarding_date TEXT,
    kyc_verified INTEGER DEFAULT 0,
    client_type TEXT,
    total_spent REAL DEFAULT 0
);

CREATE TABLE accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id INTEGER,
    iban TEXT UNIQUE,
    balance REAL NOT NULL DEFAULT 0,
    account_type TEXT NOT NULL,
    interest_rate REAL,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE cards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    card_number TEXT UNIQUE,
    holder_name TEXT,
    expiration_date TEXT,
    cvv TEXT,
    active INTEGER DEFAULT 1,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_account_id INTEGER NOT NULL,
    destination_account_id INTEGER NOT NULL,
    amount REAL NOT NULL,
    timestamp TEXT DEFAULT (datetime('now')),
    description TEXT,
    FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    FOREIGN KEY (destination_account_id) REFERENCES accounts(id)
);
