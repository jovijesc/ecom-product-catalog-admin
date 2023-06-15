package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.infrastructure.configuration.properties.storage.StorageProperties;
import com.ecom.catalog.admin.infrastructure.services.StorageService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DefaultProductImageGateway implements ProductImageGateway {


    private final String locationPattern;
    private final StorageService storageService;

    public DefaultProductImageGateway(final StorageProperties props, StorageService storageService) {
        this.locationPattern = props.getLocationPattern();
        this.storageService = storageService;
    }

    @Override
    public ProductImage create(Store store, ProductID anId, ProductImage image) {
        this.storageService.store(Set.of(toProductImageWithLocation(store, anId, image)));
        return image;
    }

    @Override
    public Set<ProductImage> create(Store store, ProductID anId, Set<ProductImage> images) {
        final var imagesWithPath = images.stream()
                .map(img -> {
                    return toProductImageWithLocation(store, anId, img);
                })
                .collect(Collectors.toSet());
        this.storageService.store(imagesWithPath);
        return images;
    }

    private ProductImage toProductImageWithLocation(Store store, ProductID anId, ProductImage img) {
        return ProductImage.with(
                img.getChecksum(),
                img.getContent(),
                img.getName(),
                filepath(store, anId, img.getName()),
                img.isFeatured());
    }

    @Override
    public Optional<ProductImage> getImage(Store store, ProductID anProductId, ProductImageID anId) {
        return Optional.empty();
    }

    @Override
    public void clearImages(Store store, ProductID anId) {

    }

    private String folder(final Store store, final ProductID anId) {
        return locationPattern.replace("{storeId}", store.getId()).replace("{productId}", anId.getValue());
    }

    private String filepath(Store store, ProductID anId, String fileName) {
        return folder(store, anId)
                .concat("/")
                .concat(fileName);
    }
}
