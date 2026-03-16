--liquibase formatted sql
--changeset dabrowskiw:8 dbms:mysql
CREATE INDEX idx_product_offer_log_ean ON product_offer_log(product_ean);
CREATE INDEX idx_product_offer_log_fetched_at ON product_offer_log(fetched_at);

--changeset dabrowskiw:8-h2 dbms:h2
CREATE INDEX idx_product_offer_log_ean ON product_offer_log(product_ean);
CREATE INDEX idx_product_offer_log_fetched_at ON product_offer_log(fetched_at);