package com.ecom.catalog.admin.application.product.image.upload;

import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImageGateway;

import java.util.Objects;

public class DefaultUploadProductImagesUseCase extends UploadProductImagesUseCase {

    private final ProductImageGateway productImageGateway;

    private final ProductGateway productGateway;

    public DefaultUploadProductImagesUseCase(final ProductImageGateway productImageGateway, final ProductGateway productGateway) {
        this.productImageGateway = Objects.requireNonNull(productImageGateway);
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public UploadProductImagesOutput execute(UploadProductImagesCommand aCommand) {
        final var anId = ProductID.from(aCommand.productId());
        final var images = aCommand.images();

        final var aProduct = this.productGateway.findById(anId)
                .orElseThrow(() -> notFound(anId));

        aProduct.updateImages(productImageGateway.create(aProduct.getStore(), anId, images));

        return UploadProductImagesOutput.with(productGateway.update(aProduct));
    }

    private NotFoundException notFound(final ProductID anId) {
        return NotFoundException.with(Product.class, anId);
    }
}
