package com.ecom.catalog.admin.application.product.image.get;

import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductImageGateway;
import com.ecom.catalog.admin.domain.product.ProductImageID;
import com.ecom.catalog.admin.domain.validation.Error;

import java.util.Objects;

public class DefaultGetProductImageUseCase extends GetProductImageUseCase {

    private ProductGateway productGateway;
    private ProductImageGateway productImageGateway;

    public DefaultGetProductImageUseCase(final ProductGateway productGateway, final ProductImageGateway productImageGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.productImageGateway = Objects.requireNonNull(productImageGateway);
    }

    @Override
    public ProductImageOutput execute(final GetProductImageCommand aCommand) {
        final var anId = ProductImageID.from(aCommand.imageId());
        final var aProduct = this.productGateway.findByImageId(anId)
                .orElseThrow(() -> notFound(anId.getValue()));
        final var aStore = aProduct.getStore();

        final var anImage = this.productImageGateway.getImage(aStore, aProduct.getId(), anId)
                .orElseThrow(() -> notFound(anId.getValue()));

        return ProductImageOutput.with(anImage);
    }

    private NotFoundException notFound(final String anId) {
        return NotFoundException.with(new Error("Image with ID %s not found".formatted(anId)));
    }
}
