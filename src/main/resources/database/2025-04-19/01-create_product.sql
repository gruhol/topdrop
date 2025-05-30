--liquibase formatted sql
--changeset dabrowskiw:1
CREATE TABLE product(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ean VARCHAR(15) NOT NULL UNIQUE,
    isbn VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    release_date VARCHAR(255),
    status VARCHAR(255),
    img VARCHAR(255),
    author VARCHAR(255),
    series VARCHAR(255),
    translator VARCHAR(255),
    category VARCHAR(255),
    publisher VARCHAR(255),
    description CLOB,
    release_year VARCHAR(10),
    cover_type VARCHAR(50),
    pages_number INT,
    width INT,
    height INT,
    edition INT,
    weight INT,
    vat VARCHAR(10),
    price DOUBLE,
    type VARCHAR(50),
    depth DOUBLE,
    approval_number VARCHAR(100),
    pcn VARCHAR(20),
    manufacturing_country_code VARCHAR(10)

);