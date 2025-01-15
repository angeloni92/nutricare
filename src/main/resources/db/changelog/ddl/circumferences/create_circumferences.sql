-- CREATE TABLE

CREATE TABLE circumferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                                        -- Identificativo univoco per ogni record di plicometria
    anthropometry_id BIGINT NOT NULL UNIQUE,                                     -- FK che punta alla tabella "anthropometries"
    chest DOUBLE NOT NULL CHECK (chest > 0),                                  		 -- Circonferenza petto (deve essere maggiore di 0)
    arm DOUBLE NOT NULL CHECK (arm > 0),                                  		 	 -- Circonferenza braccio (deve essere maggiore di 0)
    waist DOUBLE NOT NULL CHECK (waist > 0),                                  		 -- Circonferenza vita (deve essere maggiore di 0)
    hip DOUBLE NOT NULL CHECK (hip > 0),                                  		 	 -- Circonferenza fianchi (deve essere maggiore di 0)
    thigh DOUBLE NOT NULL CHECK (thigh > 0),                                  		 -- Circonferenza coscia (deve essere maggiore di 0)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                              -- Data di creazione del record
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- Data di aggiornamento
);

ALTER TABLE circumferences
ADD CONSTRAINT fk_anthropometry_id_circu FOREIGN KEY (anthropometry_id) REFERENCES anthropometries(id);


