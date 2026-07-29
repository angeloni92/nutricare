CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    occurred_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id     BIGINT,
    username    VARCHAR(255),
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id   BIGINT,
    details     VARCHAR(500),
    outcome     VARCHAR(20)  NOT NULL DEFAULT 'OK'
);