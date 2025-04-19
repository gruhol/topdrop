--liquibase formatted sql
--changeset dabrowskiw:1
CREATE TABLE product(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(300) NOT NULL,
    EAN VARCHAR(300) NOT NULL UNIQUE
);