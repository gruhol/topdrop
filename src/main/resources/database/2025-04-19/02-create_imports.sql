--liquibase formatted sql
--changeset dabrowskiw:2
CREATE TABLE import_raport(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_record INT,
    import_date TIMESTAMP,
    import_status VARCHAR(10),
    import_error_message TEXT
);