package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.ValueObject;

import java.util.Objects;

public class Store extends ValueObject implements Cloneable {
    private final String id;
    private final String name;

    private Store(final String id, final String name) {
        this.id = Objects.requireNonNull(id, "'id' should not be null");
        this.name = name;
    }

    public static Store with(final String id, final String name) {
        return new Store(id, name);
    }

    public static Store from(final String id) {
        return with(id, null);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return getId().equals(store.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public Store clone()  {
        try {
            return (Store) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
