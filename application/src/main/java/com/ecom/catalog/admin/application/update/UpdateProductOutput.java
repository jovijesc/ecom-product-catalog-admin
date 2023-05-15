package com.ecom.catalog.admin.application.update;

import com.ecom.catalog.admin.application.create.CreateProductOutput;
import com.ecom.catalog.admin.domain.product.Product;

public record UpdateProductOutput(String id) {

    public static UpdateProductOutput from(final Product aProduct) {
        return new UpdateProductOutput(aProduct.getId().getValue());
    }
}
