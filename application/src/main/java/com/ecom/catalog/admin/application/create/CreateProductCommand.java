package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;

public record CreateProductCommand(
        String name,
        String description,
        Money price,
        int stock
) {

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock
    ) {
        return new CreateProductCommand(aName, aDescription, aPrice, aStock);
    }
}
