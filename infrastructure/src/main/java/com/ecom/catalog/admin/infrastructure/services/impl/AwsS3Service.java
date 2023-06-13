package com.ecom.catalog.admin.infrastructure.services.impl;

import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.infrastructure.services.StorageService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AwsS3Service implements StorageService {

    @Override
    public Optional<ProductImage> get(String name) {
        return Optional.empty();
    }

    @Override
    public List<String> list(String prefix) {
        return null;
    }

    @Override
    public void store(Set<ProductImage> images) {

    }

    @Override
    public void delete(String name) {

    }

    @Override
    public void deleteAll(Collection<String> names) {

    }
}
