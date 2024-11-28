-- CREATE TABLE

CREATE TABLE diet_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 										-- Identificativo univoco per ogni cliente
    client_id BIGINT NOT NULL,  								    			-- FK user
    activity_level VARCHAR(255) NOT NULL,     									-- livello attività del cliente
    primary_goal VARCHAR(255) NOT NULL,     									-- obiettivo
    dietary_preference VARCHAR(255) NOT NULL,     								-- preferenze alimentari
    calory_target INT,															-- calorie in kcal
    month VARCHAR(255) NOT NULL,     											-- mese specifico per stagionalità ingredienti
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 							-- Data di creazione del cliente
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 	-- Data di aggiornamento
);

ALTER TABLE diet_details
ADD CONSTRAINT fk_client_id_diet FOREIGN KEY (client_id) REFERENCES clients(id);

