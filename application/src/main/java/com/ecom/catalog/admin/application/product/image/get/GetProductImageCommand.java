package com.ecom.catalog.admin.application.product.image.get;

public record GetProductImageCommand(String imageId) {

    public static GetProductImageCommand with(final String anId) {
        return new GetProductImageCommand(anId);
    }
}
