CREATE TABLE products(
     id VARCHAR(36) NOT NULL PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description VARCHAR(4000) NOT NULL,
     price_amount DECIMAL(17,4) NOT NULL,
     price_currency VARCHAR(3) NOT NULL,
     stock INT NOT NULL,
     status VARCHAR(10) NOT NULL,
     category_id VARCHAR(36) NOT NULL,
     created_at DATETIME(6) NOT NULL,
     updated_at DATETIME(6) NOT NULL,
     CONSTRAINT fk_p_category_id FOREIGN KEY (category_id) REFERENCES categories (id)
);