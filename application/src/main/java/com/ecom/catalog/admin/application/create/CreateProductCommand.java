package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;

public record CreateProductCommand(
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock
) {

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock
    ) {
        return new CreateProductCommand(aName, aDescription, ProductStatus.ACTIVE, aPrice, aStock);
    }

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock
    ) {
        return new CreateProductCommand(aName, aDescription, aStatus, aPrice, aStock);
    }
}