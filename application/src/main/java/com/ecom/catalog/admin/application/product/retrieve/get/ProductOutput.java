package com.ecom.catalog.admin.application.product.retrieve.get;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductStatus;

import java.time.Instant;
import java.util.Set;

public record ProductOutput(
        String id,
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock,
        String category,
        String store,
        Set<ProductImage> images,
        Instant createdAt,
        Instant updatedAt
        ) {

    public static ProductOutput from(final Product aProduct) {
        return new ProductOutput(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getStatus(),
                aProduct.getPrice(),
                aProduct.getStock(),
                aProduct.getCategoryId().getValue(),
                aProduct.getStore().getId(),
                aProduct.getImages(),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt()
        );
    }
}
