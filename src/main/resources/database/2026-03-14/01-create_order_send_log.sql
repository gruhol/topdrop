--liquibase formatted sql

--changeset dabrowskiw:12 dbms:mysql
CREATE TABLE order_send_log (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    order_number    BIGINT      NOT NULL,
    request         TEXT,
    request_date    TIMESTAMP   NOT NULL,
    response        TEXT,
    status          VARCHAR(20) NOT NULL,
    error_message   TEXT,
    PRIMARY KEY (id)
);

--changeset dabrowskiw:13 dbms:mysql
CREATE INDEX idx_order_send_log_order_number ON order_send_log (order_number);
CREATE INDEX idx_order_send_log_status       ON order_send_log (status);
CREATE INDEX idx_order_send_log_request_date ON order_send_log (request_date);

--changeset dabrowskiw:12-h2 dbms:h2
CREATE TABLE order_send_log (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    order_number    BIGINT      NOT NULL,
    request         TEXT,
    request_date    TIMESTAMP   NOT NULL,
    response        TEXT,
    status          VARCHAR(20) NOT NULL,
    error_message   TEXT,
    PRIMARY KEY (id)
);

--changeset dabrowskiw:13-h2 dbms:h2
CREATE INDEX idx_order_send_log_order_number ON order_send_log (order_number);
CREATE INDEX idx_order_send_log_status       ON order_send_log (status);
CREATE INDEX idx_order_send_log_request_date ON order_send_log (request_date);
