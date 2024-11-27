-- CREATE TABLE

CREATE TABLE ai (
    id INT AUTO_INCREMENT PRIMARY KEY, 											-- Identificativo univoco per ogni utente
    name VARCHAR(255) NOT NULL,  								    			-- Nome AI
    model VARCHAR(255) NOT NULL,         										-- Modello AI
    user_key VARCHAR(255),
    CONSTRAINT unique_name_model UNIQUE (name, model)
);
