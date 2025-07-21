--liquibase formatted sql
--changeset dabrowskiw:3 dbms:mysql
CREATE TABLE product_offer_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_ean VARCHAR(15) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    wholesale_net_price DOUBLE,
    wholesale_gross_price DOUBLE,
    discount_percent DOUBLE,
    stock INT,
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--liquibase formatted sql
--changeset dabrowskiw:3-h2 dbms:h2
CREATE TABLE product_offer_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_ean VARCHAR(15) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    wholesale_net_price DOUBLE,
    wholesale_gross_price DOUBLE,
    discount_percent DOUBLE,
    stock INT,
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);