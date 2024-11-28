-- CREATE TABLE

CREATE TABLE ai_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 										-- Identificativo univoco per ogni utente
    user_id BIGINT NOT NULL,
    ai_id BIGINT NOT NULL,
    ai_key VARCHAR(255)
);

ALTER TABLE ai_users
ADD CONSTRAINT fk_user_id_ai_users FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE ai_users
ADD CONSTRAINT fk_ai_id_ai_users FOREIGN KEY (ai_id) REFERENCES ai(id);

ALTER TABLE ai_users
ADD CONSTRAINT unique_user_id_ai_id UNIQUE (user_id, ai_id);
