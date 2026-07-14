-- CREATE TABLE

CREATE TABLE copilot_connections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    github_user_id BIGINT NOT NULL,
    github_login VARCHAR(255) NOT NULL,
    organization VARCHAR(255),
    encrypted_access_token VARCHAR(4096) NOT NULL,
    encrypted_refresh_token VARCHAR(4096),
    token_type VARCHAR(50),
    scope VARCHAR(1024),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE copilot_connections
ADD CONSTRAINT fk_user_id_copilot_connections FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE copilot_connections
ADD CONSTRAINT unique_user_provider_copilot UNIQUE (user_id, provider);

ALTER TABLE copilot_connections
ADD CONSTRAINT unique_provider_github_user_copilot UNIQUE (provider, github_user_id);

