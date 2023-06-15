package com.ecom.catalog.admin.infrastructure.services;

import com.ecom.catalog.admin.domain.product.ProductImage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StorageService {

    Optional<ProductImage> get(final String name);
    void store(Set<ProductImage> images);
    void delete(String name);
    void deleteAll(final Collection<String> names);
}
