-- CREATE TABLE

CREATE TABLE anthropometries (
    id INT AUTO_INCREMENT PRIMARY KEY, 											-- Identificativo univoco per ogni cliente
    client_id INT NOT NULL,  								    				-- FK client
    height INT NOT NULL CHECK (height > 0),         							-- Altezza del cliente
    weight DOUBLE NOT NULL CHECK (weight > 0),     								-- Peso del cliente
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 							-- Data di creazione del cliente
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 	-- Data di aggiornamento
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES clients(id)
);

