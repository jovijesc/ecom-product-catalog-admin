package com.ecom.catalog.admin.application.product.retrieve.list;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductStatus;

import java.time.Instant;

public record ProductListOutput(
        String id,
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock,
        String category,
        Instant createdAt,
        String store
) {

    public static ProductListOutput from(final Product aProduct) {
        return new ProductListOutput(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getStatus(),
                aProduct.getPrice(),
                aProduct.getStock(),
                aProduct.getCategoryId().getValue(),
                aProduct.getCreatedAt(),
                aProduct.getStore().getId()
        );
    }
}
