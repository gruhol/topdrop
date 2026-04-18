--liquibase formatted sql

--changeset dabrowskiw:15 dbms:mysql
ALTER TABLE orders ADD COLUMN platon_package_number TEXT;

--changeset dabrowskiw:15-h2 dbms:h2
ALTER TABLE orders ADD COLUMN platon_package_number TEXT;