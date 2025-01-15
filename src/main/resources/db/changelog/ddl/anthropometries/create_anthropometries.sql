-- CREATE TABLE

CREATE TABLE anthropometries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 											-- Identificativo univoco per ogni cliente
    client_id BIGINT NOT NULL,  								    				-- FK client
    height DOUBLE NOT NULL CHECK (height > 0),         							-- Altezza del cliente
    weight DOUBLE NOT NULL CHECK (weight > 0),     								-- Peso del cliente
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 							-- Data di creazione del cliente
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 	-- Data di aggiornamento
);

ALTER TABLE anthropometries
ADD CONSTRAINT fk_client_id_antro FOREIGN KEY (client_id) REFERENCES clients(id);

