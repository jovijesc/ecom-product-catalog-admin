package com.ecom.catalog.admin.application.product.update;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductStatus;

import java.util.Set;

public record UpdateProductCommand(
        String id,
        String name,
        String description,
        ProductStatus status,
        Money price,
        int stock,
        String category,
        String store,
        Set<ProductImage> images
) {

    public static UpdateProductCommand with(
            final String anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final String aCategory,
            final String aStore,
            final Set<ProductImage> images
    ) {
        return new UpdateProductCommand(anId, aName, aDescription, ProductStatus.ACTIVE, aPrice, aStock, aCategory, aStore, images);
    }

    public static UpdateProductCommand with(
            final String anId,
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock,
            final String aCategory,
            final String aStore,
            final Set<ProductImage> images
    ) {
        return new UpdateProductCommand(anId, aName, aDescription, aStatus, aPrice, aStock, aCategory, aStore, images);
    }
}

