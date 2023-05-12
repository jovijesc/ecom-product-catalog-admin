package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.Identifier;
import jdk.jfr.Category;

import java.util.Objects;
import java.util.UUID;

public class ProductID extends Identifier {

    private final String value;

    public ProductID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static ProductID unique() {
        return new ProductID(UUID.randomUUID().toString().toLowerCase());
    }

    public static ProductID from(final String anId) {
        return new ProductID(anId);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductID productID = (ProductID) o;
        return getValue().equals(productID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
