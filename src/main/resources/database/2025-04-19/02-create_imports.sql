--liquibase formatted sql
--changeset dabrowskiw:2 dbms:mysql
CREATE TABLE import_product_raport(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_added_record INT,
    import_updated_record INT,
    import_type VARCHAR(10),
    import_date TIMESTAMP,
    import_status VARCHAR(10),
    import_error_message TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

--liquibase formatted sql
--changeset dabrowskiw:2-h2 dbms:h2
CREATE TABLE import_product_raport(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_added_record INT,
    import_updated_record INT,
    import_type VARCHAR(10),
    import_date TIMESTAMP,
    import_status VARCHAR(10),
    import_error_message TEXT
);