package com.ecom.catalog.admin.application.product.update;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;

public record UpdateProductCommand(
        String id,
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock,
        String category,
        String store
) {

    public static UpdateProductCommand with(
            final String anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final String aCategory,
            final String aStore
    ) {
        return new UpdateProductCommand(anId, aName, aDescription, ProductStatus.ACTIVE, aPrice, aStock, aCategory, aStore);
    }

    public static UpdateProductCommand with(
            final String anId,
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock,
            final String aCategory,
            final String aStore
    ) {
        return new UpdateProductCommand(anId, aName, aDescription, aStatus, aPrice, aStock, aCategory, aStore);
    }
}

