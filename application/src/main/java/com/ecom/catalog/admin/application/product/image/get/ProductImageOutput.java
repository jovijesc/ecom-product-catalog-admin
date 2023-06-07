package com.ecom.catalog.admin.application.product.image.get;

import com.ecom.catalog.admin.domain.product.ProductImage;

public record ProductImageOutput(
        byte[] content,
        String name
) {

    public static ProductImageOutput with(final ProductImage anImage) {
        return new ProductImageOutput(anImage.getContent(), anImage.getName());
    }
}
