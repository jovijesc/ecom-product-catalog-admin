package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.domain.product.Product;

public record CreateProductOutput(
        String id
) {

    public static CreateProductOutput from(final String anId) {
        return new CreateProductOutput(anId);
    }

    public static CreateProductOutput from(final Product aProduct) {
        return new CreateProductOutput(aProduct.getId().getValue());
    }
}
