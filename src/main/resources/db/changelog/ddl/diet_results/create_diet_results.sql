-- CREATE TABLE

CREATE TABLE diet_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    generated_diet LONGTEXT NOT NULL,
    ai_model VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE diet_results
ADD CONSTRAINT fk_user_id_diet_results FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE diet_results
ADD CONSTRAINT unique_user_client_created UNIQUE (user_id, client_id, created_at);

