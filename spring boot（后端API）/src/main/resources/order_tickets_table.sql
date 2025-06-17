CREATE TABLE order_tickets (
    order_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    count INT NOT NULL,
    PRIMARY KEY (order_id, name),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
