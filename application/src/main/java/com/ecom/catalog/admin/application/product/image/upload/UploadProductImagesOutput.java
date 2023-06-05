package com.ecom.catalog.admin.application.product.image.upload;

import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.utils.CollectionUtils;

import java.util.Set;

public record UploadProductImagesOutput(
        String productId,
        Set<String> imagesIds
) {

    public static UploadProductImagesOutput with(final Product aProduct) {
        return new UploadProductImagesOutput(aProduct.getId().getValue(),
                CollectionUtils.mapTo(aProduct.getImages(), productImage -> productImage.getId().getValue()));
    }
}
