--liquibase formatted sql
--changeset dabrowskiw:7 dbms:mysql
ALTER TABLE product
    ADD COLUMN gpsr_contractor_name         VARCHAR(255),
    ADD COLUMN gpsr_contractor_country_code VARCHAR(10),
    ADD COLUMN gpsr_street                  VARCHAR(255),
    ADD COLUMN gpsr_house_number            VARCHAR(20),
    ADD COLUMN gpsr_apartment_number        VARCHAR(20),
    ADD COLUMN gpsr_postal_code             VARCHAR(20),
    ADD COLUMN gpsr_city                    VARCHAR(255),
    ADD COLUMN gpsr_email                   VARCHAR(255);

--changeset dabrowskiw:7-h2 dbms:h2
ALTER TABLE product
    ADD COLUMN gpsr_contractor_name         VARCHAR(255);
ALTER TABLE product
    ADD COLUMN gpsr_contractor_country_code VARCHAR(10);
ALTER TABLE product
    ADD COLUMN gpsr_street                  VARCHAR(255);
ALTER TABLE product
    ADD COLUMN gpsr_house_number            VARCHAR(20);
ALTER TABLE product
    ADD COLUMN gpsr_apartment_number        VARCHAR(20);
ALTER TABLE product
    ADD COLUMN gpsr_postal_code             VARCHAR(20);
ALTER TABLE product
    ADD COLUMN gpsr_city                    VARCHAR(255);
ALTER TABLE product
    ADD COLUMN gpsr_email                   VARCHAR(255);