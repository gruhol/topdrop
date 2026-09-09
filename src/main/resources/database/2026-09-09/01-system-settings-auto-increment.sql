--liquibase formatted sql

--changeset dabrowskiw:16 dbms:mysql
ALTER TABLE system_settings MODIFY COLUMN id BIGINT AUTO_INCREMENT;

--changeset dabrowskiw:16-h2 dbms:h2
ALTER TABLE system_settings ALTER COLUMN id BIGINT AUTO_INCREMENT;

--changeset dabrowskiw:17-h2 dbms:h2
ALTER TABLE system_settings ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM system_settings);
