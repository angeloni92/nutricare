-- CREATE TABLE

CREATE TABLE circumferences (
    id INT AUTO_INCREMENT PRIMARY KEY,                                           -- Identificativo univoco per ogni record di plicometria
    anthropometry_id INT NOT NULL UNIQUE,                                        -- FK che punta alla tabella "anthropometries"
    chest INT NOT NULL CHECK (chest > 0),                                  		 -- Circonferenza petto (deve essere maggiore di 0)
    arm INT NOT NULL CHECK (arm > 0),                                  		 	 -- Circonferenza braccio (deve essere maggiore di 0)
    waist INT NOT NULL CHECK (waist > 0),                                  		 -- Circonferenza vita (deve essere maggiore di 0)
    hip INT NOT NULL CHECK (hip > 0),                                  		 	 -- Circonferenza fianchi (deve essere maggiore di 0)
    thigh INT NOT NULL CHECK (thigh > 0),                                  		 -- Circonferenza coscia (deve essere maggiore di 0)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                              -- Data di creazione del record
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- Data di aggiornamento
    CONSTRAINT fk_anthropometry_id FOREIGN KEY (anthropometry_id) REFERENCES anthropometries(id)  -- Vincolo FK verso la tabella "anthropometries"
);


