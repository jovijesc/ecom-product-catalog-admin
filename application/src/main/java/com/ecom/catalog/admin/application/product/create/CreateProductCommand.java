package com.ecom.catalog.admin.application.product.create;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;

public record CreateProductCommand(
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock,
        String category,
        String store
) {

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final String aCategory,
            final String aStore
    ) {
        return new CreateProductCommand(aName, aDescription, ProductStatus.ACTIVE, aPrice, aStock, aCategory, aStore);
    }

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock,
            final String aCategory,
            final String aStore
    ) {
        return new CreateProductCommand(aName, aDescription, aStatus, aPrice, aStock, aCategory, aStore);
    }
}
