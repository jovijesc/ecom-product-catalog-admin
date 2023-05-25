package com.ecom.catalog.admin.domain.product;

import java.util.Arrays;
import java.util.Optional;

public enum ProductStatus {

    ACTIVE,
    INACTIVE;

    public static Optional<ProductStatus> of(final String label) {
        return Arrays.stream(ProductStatus.values())
                .filter(it -> it.name().equalsIgnoreCase(label))
                .findFirst();
    }
}
