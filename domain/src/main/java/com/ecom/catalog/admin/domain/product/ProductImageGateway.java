package com.ecom.catalog.admin.domain.product;

import java.util.Optional;
import java.util.Set;

public interface ProductImageGateway {


    ProductImage create(Store store, ProductID anId, ProductImage image);

    Set<ProductImage> create(Store store, ProductID anId, Set<ProductImage> images);

    Optional<ProductImage> getImage(Store store, ProductID anProductId, ProductImageID anId);

    void clearImages(Store store, ProductID anId);
}
