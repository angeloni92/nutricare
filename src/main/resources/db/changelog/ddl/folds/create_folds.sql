-- CREATE TABLE

CREATE TABLE folds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                                        -- Identificativo univoco per ogni record di plicometria
    anthropometry_id BIGINT NOT NULL UNIQUE,                                     -- FK che punta alla tabella "anthropometries"
    pectoral DOUBLE NOT NULL CHECK (pectoral > 0),                                  -- Plica pettorale (deve essere maggiore di 0)
    axillary DOUBLE NOT NULL CHECK (axillary > 0),                                  -- Plica ascellare (deve essere maggiore di 0)
    suprailiac DOUBLE NOT NULL CHECK (suprailiac > 0),                              -- Plica soprailiaca (deve essere maggiore di 0)
    abdominal DOUBLE NOT NULL CHECK (abdominal > 0),                                -- Plica addominale (deve essere maggiore di 0)
    triceps DOUBLE NOT NULL CHECK (triceps > 0),                                    -- Plica tricipite (deve essere maggiore di 0)
    subscapularis DOUBLE NOT NULL CHECK (subscapularis > 0),                        -- Plica sottoscapolare (deve essere maggiore di 0)
    thigh DOUBLE NOT NULL CHECK (thigh > 0),                                        -- Plica coscia (deve essere maggiore di 0)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                              -- Data di creazione del record
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- Data di aggiornamento
    CONSTRAINT fk_anthropometry_id FOREIGN KEY (anthropometry_id) REFERENCES anthropometries(id)  -- Vincolo FK verso la tabella "anthropometries"
);

ALTER TABLE folds
ADD CONSTRAINT fk_anthropometry_id_folds FOREIGN KEY (anthropometry_id) REFERENCES anthropometries(id);


