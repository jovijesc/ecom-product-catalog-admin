package com.ecom.catalog.admin.application.product.image.upload;

import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImageGateway;

import java.util.Objects;

public class DefaultUploadProductImagesUseCase extends UploadProductImagesUseCase {

    private final ProductGateway productGateway;
    private final ProductImageGateway productImageGateway;

    public DefaultUploadProductImagesUseCase(final ProductGateway productGateway, final ProductImageGateway productImageGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.productImageGateway = Objects.requireNonNull(productImageGateway);
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
