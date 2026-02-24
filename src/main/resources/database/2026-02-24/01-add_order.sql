--liquibase formatted sql

--changeset dabrowskiw:9 dbms:mysql
CREATE TABLE orders (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    order_id                BIGINT          NOT NULL,
    shop_order_id           BIGINT,
    external_order_id       VARCHAR(100),
    order_source            VARCHAR(50),
    order_source_id         BIGINT,
    order_source_info       VARCHAR(255),
    order_status_id         BIGINT,
    confirmed               BOOLEAN         NOT NULL DEFAULT FALSE,
    date_confirmed          TIMESTAMP,
    date_add                TIMESTAMP,
    date_in_status          TIMESTAMP,
    user_login              VARCHAR(100),
    phone                   VARCHAR(30),
    email                   VARCHAR(150),
    user_comments           TEXT,
    admin_comments          TEXT,
    currency                VARCHAR(10),
    payment_method          VARCHAR(100),
    payment_method_cod      VARCHAR(5),
    payment_done            DECIMAL(10, 2),
-- delivery
    delivery_method_id      BIGINT,
    delivery_method         VARCHAR(100),
    delivery_price          DECIMAL(10, 2),
    delivery_package_module VARCHAR(100),
    delivery_package_nr     VARCHAR(100),
    delivery_fullname       VARCHAR(150),
    delivery_company        VARCHAR(150),
    delivery_address        VARCHAR(200),
    delivery_city           VARCHAR(100),
    delivery_state          VARCHAR(100),
    delivery_postcode       VARCHAR(20),
    delivery_country_code   VARCHAR(10),
    delivery_country        VARCHAR(100),
    delivery_point_id       VARCHAR(100),
    delivery_point_name     VARCHAR(100),
    delivery_point_address  VARCHAR(200),
    delivery_point_postcode VARCHAR(20),
    delivery_point_city     VARCHAR(100),
-- invoice
    invoice_fullname        VARCHAR(150),
    invoice_company         VARCHAR(150),
    invoice_nip             VARCHAR(30),
    invoice_address         VARCHAR(200),
    invoice_city            VARCHAR(100),
    invoice_state           VARCHAR(100),
    invoice_postcode        VARCHAR(20),
    invoice_country_code    VARCHAR(10),
    invoice_country         VARCHAR(100),
    want_invoice            BOOLEAN         NOT NULL DEFAULT FALSE,
-- extra
    extra_field_1           TEXT,
    extra_field_2           TEXT,
    order_page              VARCHAR(300),
    pick_state              INT,
    pack_state              INT,
    star                    INT,
    PRIMARY KEY (id),
    CONSTRAINT uq_orders_order_id UNIQUE (order_id)
);

--changeset dabrowskiw:10 dbms:mysql
CREATE TABLE order_products (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    order_id         BIGINT NOT NULL,
    order_product_id BIGINT NOT NULL,
    storage          VARCHAR(50),
    storage_id       BIGINT,
    product_id       VARCHAR(100),
    variant_id       VARCHAR(100),
    name             VARCHAR(500),
    attributes       TEXT,
    sku              VARCHAR(100),
    ean              VARCHAR(50),
    location         VARCHAR(100),
    warehouse_id     BIGINT,
    auction_id       VARCHAR(100),
    price_brutto     DECIMAL(10, 2),
    tax_rate         DECIMAL(5, 2),
    quantity         INT,
    weight           DECIMAL(8, 3),
    bundle_id        BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_order_products_order_product_id UNIQUE (order_product_id),
    CONSTRAINT fk_order_products_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

--changeset dabrowskiw:11 dbms:mysql
CREATE INDEX idx_orders_order_source    ON orders (order_source);
CREATE INDEX idx_orders_order_status_id ON orders (order_status_id);
CREATE INDEX idx_orders_email           ON orders (email);
CREATE INDEX idx_orders_date_add        ON orders (date_add);
CREATE INDEX idx_order_products_order_id ON order_products (order_id);
CREATE INDEX idx_order_products_ean     ON order_products (ean);

--changeset dabrowskiw:9-h2 dbms:h2
CREATE TABLE orders (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    order_id                BIGINT          NOT NULL,
    shop_order_id           BIGINT,
    external_order_id       VARCHAR(100),
    order_source            VARCHAR(50),
    order_source_id         BIGINT,
    order_source_info       VARCHAR(255),
    order_status_id         BIGINT,
    confirmed               BOOLEAN         NOT NULL DEFAULT FALSE,
    date_confirmed          TIMESTAMP,
    date_add                TIMESTAMP,
    date_in_status          TIMESTAMP,
    user_login              VARCHAR(100),
    phone                   VARCHAR(30),
    email                   VARCHAR(150),
    user_comments           TEXT,
    admin_comments          TEXT,
    currency                VARCHAR(10),
    payment_method          VARCHAR(100),
    payment_method_cod      VARCHAR(5),
    payment_done            DECIMAL(10, 2),
-- delivery
    delivery_method_id      BIGINT,
    delivery_method         VARCHAR(100),
    delivery_price          DECIMAL(10, 2),
    delivery_package_module VARCHAR(100),
    delivery_package_nr     VARCHAR(100),
    delivery_fullname       VARCHAR(150),
    delivery_company        VARCHAR(150),
    delivery_address        VARCHAR(200),
    delivery_city           VARCHAR(100),
    delivery_state          VARCHAR(100),
    delivery_postcode       VARCHAR(20),
    delivery_country_code   VARCHAR(10),
    delivery_country        VARCHAR(100),
    delivery_point_id       VARCHAR(100),
    delivery_point_name     VARCHAR(100),
    delivery_point_address  VARCHAR(200),
    delivery_point_postcode VARCHAR(20),
    delivery_point_city     VARCHAR(100),
-- invoice
    invoice_fullname        VARCHAR(150),
    invoice_company         VARCHAR(150),
    invoice_nip             VARCHAR(30),
    invoice_address         VARCHAR(200),
    invoice_city            VARCHAR(100),
    invoice_state           VARCHAR(100),
    invoice_postcode        VARCHAR(20),
    invoice_country_code    VARCHAR(10),
    invoice_country         VARCHAR(100),
    want_invoice            BOOLEAN         NOT NULL DEFAULT FALSE,
-- extra
    extra_field_1           TEXT,
    extra_field_2           TEXT,
    order_page              VARCHAR(300),
    pick_state              INT,
    pack_state              INT,
    star                    INT,
    PRIMARY KEY (id),
    CONSTRAINT uq_orders_order_id UNIQUE (order_id)
);

--changeset dabrowskiw:10-h2 dbms:h2
CREATE TABLE order_products (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    order_id         BIGINT NOT NULL,
    order_product_id BIGINT NOT NULL,
    storage          VARCHAR(50),
    storage_id       BIGINT,
    product_id       VARCHAR(100),
    variant_id       VARCHAR(100),
    name             VARCHAR(500),
    attributes       TEXT,
    sku              VARCHAR(100),
    ean              VARCHAR(50),
    location         VARCHAR(100),
    warehouse_id     BIGINT,
    auction_id       VARCHAR(100),
    price_brutto     DECIMAL(10, 2),
    tax_rate         DECIMAL(5, 2),
    quantity         INT,
    weight           DECIMAL(8, 3),
    bundle_id        BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_order_products_order_product_id UNIQUE (order_product_id),
    CONSTRAINT fk_order_products_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

--changeset dabrowskiw:11-h2 dbms:h2
CREATE INDEX idx_orders_order_source    ON orders (order_source);
CREATE INDEX idx_orders_order_status_id ON orders (order_status_id);
CREATE INDEX idx_orders_email           ON orders (email);
CREATE INDEX idx_orders_date_add        ON orders (date_add);
CREATE INDEX idx_order_products_order_id ON order_products (order_id);
CREATE INDEX idx_order_products_ean     ON order_products (ean);