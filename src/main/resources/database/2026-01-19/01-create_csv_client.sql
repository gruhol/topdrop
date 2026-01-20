--liquibase formatted sql
--changeset dabrowskiw:7
CREATE TABLE csv_client(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    hash VARCHAR(255) NOT NULL,
    logo VARCHAR(20) UNIQUE NOT NULL
);