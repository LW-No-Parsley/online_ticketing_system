CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    selected_date DATE NOT NULL,
    selected_time VARCHAR(255) NOT NULL,
    total_amount INT NOT NULL,
    tickets JSON NOT NULL,
    status VARCHAR(255) DEFAULT '待支付',
    created_at DATETIME NOT NULL
);
