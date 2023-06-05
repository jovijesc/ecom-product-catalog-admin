package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.Identifier;
import com.ecom.catalog.admin.domain.utils.IdUtils;

import java.util.Objects;

public class ProductImageID extends Identifier {

    private final String value;

    public ProductImageID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static com.ecom.catalog.admin.domain.product.ProductImageID unique() {
        return new com.ecom.catalog.admin.domain.product.ProductImageID(IdUtils.uuid());
    }

    public static com.ecom.catalog.admin.domain.product.ProductImageID from(final String anId) {
        return new com.ecom.catalog.admin.domain.product.ProductImageID(anId);
    }

    public static com.ecom.catalog.admin.domain.product.ProductImageID from(final ProductImage anImage) {
        return anImage.getId();
    }

    @Override
    public String getValue() {
        return value;
    }
}
