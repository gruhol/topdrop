--liquibase formatted sql
--changeset dabrowskiw:5
ALTER TABLE category
ADD CONSTRAINT uq_category_name_parent UNIQUE (name, parent_id);