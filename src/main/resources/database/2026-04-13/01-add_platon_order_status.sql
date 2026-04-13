--liquibase formatted sql

--changeset gruhol:2026-04-13-01
ALTER TABLE orders ADD COLUMN platon_order_status VARCHAR(50);