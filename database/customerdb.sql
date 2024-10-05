-- Create database
CREATE DATABASE customerdb;

-- Connect to the newly created database
\c customerdb;

-- Enable extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tables

-- Tabla Gender (Master Table for Genders)
CREATE TABLE Gender (
    id SERIAL PRIMARY KEY,
    gender_name VARCHAR(10) NOT NULL UNIQUE
);

-- Insert default values into Gender table
INSERT INTO Gender (gender_name) VALUES ('Male'), ('Female'), ('Other');

-- Tabla Person
CREATE TABLE Person (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    gender_id INT NOT NULL REFERENCES Gender(id),
    age INT CHECK (age > 0),
    identification VARCHAR(20) UNIQUE NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20)
);

-- Tabla Client
CREATE TABLE Client (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL,
    person_id UUID NOT NULL REFERENCES Person(id) ON DELETE CASCADE
);

