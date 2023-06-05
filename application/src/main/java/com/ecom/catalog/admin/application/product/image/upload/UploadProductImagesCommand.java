package com.ecom.catalog.admin.application.product.image.upload;

import com.ecom.catalog.admin.domain.product.ProductImage;

import java.util.Set;

public record UploadProductImagesCommand(
        String productId,
        Set<ProductImage> images
) {

    public static UploadProductImagesCommand with(final String productId, final Set<ProductImage> images) {
        return new UploadProductImagesCommand(productId, images);
    }
}
