--liquibase formatted sql

--changeset dabrowskiw:18 dbms:mysql
ALTER TABLE system_settings MODIFY COLUMN config_value TEXT;

--changeset dabrowskiw:18-h2 dbms:h2
ALTER TABLE system_settings ALTER COLUMN config_value CLOB;
