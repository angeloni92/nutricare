-- CREATE TABLE

CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 										-- Identificativo univoco per ogni cliente
    user_id BIGINT NOT NULL,  													-- FK user
    name VARCHAR(255) NOT NULL,         										-- Nome del cliente
    surname VARCHAR(255) NOT NULL,     											-- Cognome del cliente
    age INT NOT NULL CHECK (age > 0),       									-- Età del cliente
    country VARCHAR(255) NOT NULL,     											-- paese
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 							-- Data di creazione del cliente
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 	-- Data di aggiornamento
);

ALTER TABLE clients
ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE clients
ADD CONSTRAINT unique_name_surname UNIQUE (name, surname);


