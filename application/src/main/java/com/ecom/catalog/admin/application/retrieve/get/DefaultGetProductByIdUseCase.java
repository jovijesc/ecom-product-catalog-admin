package com.ecom.catalog.admin.application.retrieve.get;

import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;

import java.util.Objects;

public class DefaultGetProductByIdUseCase extends GetProductByIdUseCase {

    private final ProductGateway productGateway;

    public DefaultGetProductByIdUseCase(ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public ProductOutput execute(final String anIn) {
        final var aProductId = ProductID.from(anIn);
        return this.productGateway.findById(aProductId)
                .map(ProductOutput::from)
                .orElseThrow(() -> NotFoundException.with(Product.class, aProductId));
    }
}
