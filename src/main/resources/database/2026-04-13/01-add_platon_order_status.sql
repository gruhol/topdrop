--liquibase formatted sql

--changeset dabrowskiw:14 dbms:mysql
ALTER TABLE orders ADD COLUMN platon_order_status VARCHAR(50);

--changeset dabrowskiw:14-h2 dbms:h2
ALTER TABLE orders ADD COLUMN platon_order_status VARCHAR(50);