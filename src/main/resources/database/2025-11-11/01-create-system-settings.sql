--liquibase formatted sql
--changeset dabrowskiw:6
CREATE TABLE system_settings(
    id BIGINT PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value VARCHAR(255),
    value_type VARCHAR(20),
    description VARCHAR(255)
);

INSERT INTO system_settings (id, config_key, config_value, value_type, description)
VALUES (1, 'baselinker_auto_export', 'false', 'BOOLEAN', 'Autoexport to baselinker');