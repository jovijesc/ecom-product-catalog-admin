CREATE TABLE products_image (
        id VARCHAR(36) PRIMARY KEY,
        checksum VARCHAR(255) NOT NULL,
        name VARCHAR(255) NOT NULL,
        file_path VARCHAR(500) NOT NULL,
        featured BOOLEAN NOT NULL,
        product_id VARCHAR(36) NOT NULL,
        FOREIGN KEY (product_id) REFERENCES products (id)
);