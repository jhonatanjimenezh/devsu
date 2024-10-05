-- Create database
CREATE DATABASE accountdb;

-- Connect to the newly created database
\c accountdb;

-- Enable extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tables

-- Table for AccountType
CREATE TABLE AccountType (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Table for Account
CREATE TABLE Account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type_id INT NOT NULL REFERENCES AccountType(id),
    initial_balance DECIMAL(15, 2) NOT NULL CHECK (initial_balance >= 0),
    status BOOLEAN NOT NULL,
    client_id UUID NOT NULL
);

-- Table for TransactionType
CREATE TABLE TransactionType (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);


-- Table for Transaction
CREATE TABLE Transaction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_type_id INT NOT NULL REFERENCES TransactionType(id),
    amount DECIMAL(15, 2) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    account_id UUID NOT NULL REFERENCES Account(id) ON DELETE CASCADE
);


-- Insertar datos en la tabla AccountType
INSERT INTO AccountType (name) VALUES 
('Ahorros'),
('Corriente');

-- Insertar datos en la tabla TransactionType
INSERT INTO TransactionType (name) VALUES 
('Depósito'),
('Retiro');





