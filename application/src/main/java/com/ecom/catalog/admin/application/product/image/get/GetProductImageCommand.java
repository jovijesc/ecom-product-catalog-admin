package com.ecom.catalog.admin.application.product.image.get;

public record GetProductImageCommand(
        String productId,
        String imageId) {

    public static GetProductImageCommand with(final String aProductId, final String anImageId) {
        return new GetProductImageCommand(aProductId, anImageId);
    }
}
