--liquibase formatted sql
--changeset dabrowskiw:4 dbms:mysql
CREATE TABLE baselinker_export_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNIQUE,
    baselinker_id BIGINT NOT NULL,
    create_date TIMESTAMP,
    update_date TIMESTAMP
);

--liquibase formatted sql
--changeset dabrowskiw:4-h2 dbms:h2
CREATE TABLE baselinker_export_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNIQUE,
    baselinker_id BIGINT NOT NULL,
    create_date TIMESTAMP,
    update_date TIMESTAMP
);