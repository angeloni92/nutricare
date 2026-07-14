CREATE TABLE client_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    activity_level VARCHAR(255) NOT NULL,
    primary_goal VARCHAR(255) NOT NULL,
    dietary_preference VARCHAR(255) NOT NULL,
    calory_target INT,
    diet_month VARCHAR(255) NOT NULL,
    free_day VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE client_details
ADD CONSTRAINT fk_client_id_diet FOREIGN KEY (client_id) REFERENCES clients(id);

CREATE TABLE client_food_preferences (
    client_id BIGINT NOT NULL,
    food_preference VARCHAR(255),
    CONSTRAINT fk_food_pref_client FOREIGN KEY (client_id) REFERENCES client_details(id)
);

CREATE TABLE client_food_dislikes (
    client_id BIGINT NOT NULL,
    food_dislike VARCHAR(255),
    CONSTRAINT fk_food_dislike_client FOREIGN KEY (client_id) REFERENCES client_details(id)
);