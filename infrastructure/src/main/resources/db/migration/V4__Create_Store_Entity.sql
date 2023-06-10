CREATE TABLE stores (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

ALTER TABLE products
    ADD COLUMN store_id VARCHAR(36) NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT fk_product_store
        FOREIGN KEY (store_id)
            REFERENCES stores (id);
