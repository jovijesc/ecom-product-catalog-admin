package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductImageGateway;
import com.ecom.catalog.admin.domain.product.Store;
import com.ecom.catalog.admin.infrastructure.services.StorageService;
import com.ecom.catalog.admin.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@IntegrationTest
class DefaultProductImageGatewayTest {

    @Autowired
    private ProductImageGateway productImageGateway;

    @Autowired
    private StorageService storageService;

    @Test
    public void testInjection() {
        Assertions.assertNotNull(productImageGateway);
        Assertions.assertInstanceOf(DefaultProductImageGateway.class, productImageGateway);

        Assertions.assertNotNull(storageService);
        Assertions.assertInstanceOf(InMemoryStorageService.class, storageService);
    }

    @Test
    public void givenValidImages_whenCallsCreate_shouldCreateIt() {
        // given
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedProductId = ProductID.unique();
        final var expectedImage1 = Fixture.ProductImages.img01();
        final var expectedImage2 = Fixture.ProductImages.img02();
        final var expectedImages = Set.of(expectedImage1,expectedImage2);
        final var expectedNamePattern = "storeId-%s/productId-%s/%s";
        final var expectedLocations = expectedImages.stream()
                .map(img -> expectedNamePattern.formatted(expectedStore.getId(), expectedProductId.getValue(), img.getName()))
                .collect(Collectors.toList());
        final var expectedTotalImages = 2;

        // when
        final var actualImages = this.productImageGateway.create(expectedStore, expectedProductId, expectedImages);

        // then
        Assertions.assertNotNull(actualImages);
        Assertions.assertEquals(actualImages.size(), expectedTotalImages);
        Assertions.assertEquals(
                expectedImages.stream()
                        .map(ProductImage::getName)
                        .collect(Collectors.toSet()),
                actualImages.stream()
                        .map(ProductImage::getName)
                        .collect(Collectors.toSet())
        );
        Assertions.assertEquals(
                expectedLocations.stream().collect(Collectors.toSet()),
                actualImages.stream()
                        .map(ProductImage::getLocation)
                        .collect(Collectors.toSet())
        );

        final var actualImageStored1 = this.storageService.get(expectedLocations.get(0)).get();
        final var actualImageStored2 = this.storageService.get(expectedLocations.get(1)).get();

        Assertions.assertEquals(actualImageStored1.getLocation(), expectedLocations.get(0));
        Assertions.assertArrayEquals(actualImageStored1.getContent(), expectedImage1.getContent());

        Assertions.assertEquals(actualImageStored2.getLocation(), expectedLocations.get(1));
        Assertions.assertArrayEquals(actualImageStored2.getContent(), expectedImage2.getContent());

    }

    @Test
    public void givenValidParams_whenCallsGetImage_shouldReturnIt() {
        // given
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedProductId = ProductID.unique();
        final var expectedImage1 = Fixture.ProductImages.img01();
        final var expectedLocation = "storeId-%s/productId-%s/%s".formatted(expectedStore.getId(), expectedProductId.getValue(), expectedImage1.getName());
        final var expectedImageWithLocation = ProductImage.with(expectedImage1, expectedLocation);
        final var expectedImages = Set.of(expectedImageWithLocation);

        storageService.store(expectedImages);

        // when
        final var actualImage = this.productImageGateway.getImage(expectedStore, expectedProductId, expectedImageWithLocation).get();

        // then
        Assertions.assertArrayEquals(actualImage.getContent(), expectedImageWithLocation.getContent());
        Assertions.assertEquals(actualImage.getLocation(), expectedLocation);
    }

    @Test
    public void givenInvalidParams_whenCallsGetImage_shouldReturnEmpty() {
        // given
        final var productNotFound = ProductID.unique();
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedProductId = ProductID.unique();
        final var expectedImage1 = Fixture.ProductImages.img01();
        final var expectedLocation = "storeId-%s/productId-%s/%s".formatted(expectedStore.getId(), expectedProductId.getValue(), expectedImage1.getName());
        final var expectedImageWithLocation = ProductImage.with(expectedImage1, expectedLocation);
        final var expectedImages = Set.of(expectedImageWithLocation);

        storageService.store(expectedImages);

        // when
        final var actualImage = this.productImageGateway.getImage(expectedStore, productNotFound, expectedImageWithLocation);

        // then
        Assertions.assertTrue(actualImage.isEmpty());
    }

    @Test
    public void givenValidParams_whenCallsClearImages_shouldDeleteAll() {
        // given
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedProduct1 = ProductID.unique();
        final var expectedProduct2 = ProductID.unique();

        final var expectedImage1Product1 = getImage(expectedStore, expectedProduct1, Fixture.ProductImages.img01());
        final var expectedImage2Product1 = getImage(expectedStore, expectedProduct1, Fixture.ProductImages.img02());
        final var expectedImage1Product2 = getImage(expectedStore, expectedProduct2, Fixture.ProductImages.img01());

        final var imagesToBeDeleted = Set.of(expectedImage1Product1, expectedImage2Product1);

        this.storageService.store(imagesToBeDeleted);
        this.storageService.store(Set.of(expectedImage1Product2));

        // when
        this.productImageGateway.clearImages(expectedStore, expectedProduct1);

        // then
        final var actualImage1Product1 =  storageService.get(expectedImage1Product1.getLocation());
        final var actualImage2Product1 =  storageService.get(expectedImage2Product1.getLocation());

        Assertions.assertTrue(actualImage1Product1.isEmpty());
        Assertions.assertTrue(actualImage2Product1.isEmpty());

        final var actualImage1Product2 =  storageService.get(expectedImage1Product2.getLocation()).get();
        Assertions.assertEquals(actualImage1Product2.getLocation(), expectedImage1Product2.getLocation());
        Assertions.assertArrayEquals(actualImage1Product2.getContent(), expectedImage1Product2.getContent());

    }

    private String getLocation(Store aStore, ProductID aProduct, ProductImage anImage) {
        return "storeId-%s/productId-%s/%s".formatted(aStore.getId(), aProduct.getValue(), anImage.getName());
    }

    private ProductImage getImage(Store aStore, ProductID aProduct, ProductImage anImage) {
        return ProductImage.with(anImage, getLocation(aStore, aProduct, anImage));
    }

}