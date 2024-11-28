-- CREATE TABLE

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 										-- Identificativo univoco per ogni utente
    username VARCHAR(255) NOT NULL UNIQUE,  								    -- Nome utente, deve essere unico
    password VARCHAR(255) NOT NULL,         									-- La password dell'utente
    email VARCHAR(255) NOT NULL UNIQUE,     									-- Email, deve essere unica
    role VARCHAR(50) DEFAULT 'USER',       										-- Ruolo dell'utente (ad esempio 'USER', 'ADMIN')
    email_confirmed BOOLEAN NOT NULL DEFAULT FALSE, 							-- Stato di conferma dell'email
    confirmation_token VARCHAR(255) UNIQUE,                                     -- Token univoco per la conferma dell'email
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 							-- Data di creazione dell'utente
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 	-- Data di aggiornamento
);

-- CREATE INDEX
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);