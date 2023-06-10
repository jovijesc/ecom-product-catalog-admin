package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.domain.product.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class DefaultProductImageGateway implements ProductImageGateway {


    @Override
    public ProductImage create(Store store, ProductID anId, ProductImage image) {
        return null;
    }

    @Override
    public Set<ProductImage> create(Store store, ProductID anId, Set<ProductImage> images) {
        return null;
    }

    @Override
    public Optional<ProductImage> getImage(Store store, ProductID anProductId, ProductImageID anId) {
        return Optional.empty();
    }

    @Override
    public void clearImages(Store store, ProductID anId) {

    }
}
