--liquibase formatted sql
--changeset dabrowskiw:5 dbms:mysql
CREATE TABLE category(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(50),
    baselinker_id VARCHAR(50),
    send_date TIMESTAMP;
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

--liquibase formatted sql
--changeset dabrowskiw:5-h2 dbms:h2
CREATE TABLE category(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(50)
    baselinker_id VARCHAR(50),
    send_date TIMESTAMP;
);