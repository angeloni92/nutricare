-- CREATE TABLE

CREATE TABLE ai (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 										-- Identificativo univoco per ogni utente
    name VARCHAR(255) NOT NULL,  								    			-- Nome AI
    model VARCHAR(255) NOT NULL
);

ALTER TABLE ai
ADD CONSTRAINT unique_name_model UNIQUE (name, model);
