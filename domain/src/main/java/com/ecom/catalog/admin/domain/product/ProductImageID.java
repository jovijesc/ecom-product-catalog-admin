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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductImageID that = (ProductImageID) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
