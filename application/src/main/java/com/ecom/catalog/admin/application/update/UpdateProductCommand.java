package com.ecom.catalog.admin.application.update;

import com.ecom.catalog.admin.application.create.CreateProductCommand;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;

public record UpdateProductCommand(
        String id,
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock,
        String category
) {

    public static UpdateProductCommand with(
            final String anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final String aCategory
    ) {
        return new UpdateProductCommand(anId, aName, aDescription, ProductStatus.ACTIVE, aPrice, aStock, aCategory);
    }

    public static UpdateProductCommand with(
            final String anId,
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock,
            final String aCategory
    ) {
        return new UpdateProductCommand(anId, aName, aDescription, aStatus, aPrice, aStock, aCategory);
    }
}

