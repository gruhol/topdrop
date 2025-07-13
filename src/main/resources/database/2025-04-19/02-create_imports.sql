--liquibase formatted sql
--changeset dabrowskiw:2
CREATE TABLE import_raport(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_added_record INT,
    import_updated_record INT,
    import_date TIMESTAMP,
    import_status VARCHAR(10),
    import_error_message TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;