package com.ecom.catalog.admin.infrastructure.services.local;

import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.infrastructure.services.StorageService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryStorageService implements StorageService {

    private final Map<String, ProductImage> storage;

    public InMemoryStorageService() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<ProductImage> get(String name) {
        return Optional.ofNullable(this.storage.get(name));
    }

    @Override
    public void store(Set<ProductImage> images) {
        this.storage.putAll(
                images.stream()
                        .collect(Collectors.toMap(ProductImage::getLocation, Function.identity()))
        );
    }

    @Override
    public List<String> list(String prefix) {
        if( prefix == null ) {
            return Collections.emptyList();
        }
        return this.storage.keySet().stream()
                .filter( it -> it.startsWith(prefix))
                .toList();
    }

    @Override
    public void delete(String name) {
        this.storage.remove(name);
    }

    @Override
    public void deleteAll(Collection<String> names) {
        names.forEach(this.storage::remove);
    }

    public void reset() {
        this.storage.clear();
    }

    public Map<String, ProductImage> storage() {
        return storage;
    }
}
