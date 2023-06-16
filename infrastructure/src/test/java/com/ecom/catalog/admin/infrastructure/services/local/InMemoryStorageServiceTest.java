package com.ecom.catalog.admin.infrastructure.services.local;

import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.Store;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class InMemoryStorageServiceTest {

    private InMemoryStorageService storage = new InMemoryStorageService();

    @BeforeEach
    public void setUp() {
        this.storage.reset();
    }

    @Test
    public void givenValidImages_whenCallsStore_shouldStoreIt() {
        // given
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedProductId = ProductID.unique();
        final var expectedImage1 = getImage(expectedStore, expectedProductId, Fixture.ProductImages.img01());
        final var expectedImage2 = getImage(expectedStore, expectedProductId, Fixture.ProductImages.img02());
        final var expectedImages = Set.of(expectedImage1,expectedImage2);
        // when
        this.storage.store(expectedImages);

        // then
        expectedImages.forEach(img -> {
            Assertions.assertEquals(img, this.storage.get(img.getLocation()).get());
        });

    }

    @Test
    public void givenValidParams_whenCallsGet_shouldReturnIt() {
        // given
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedProductId = ProductID.unique();
        final var expectedImage1 = getImage(expectedStore, expectedProductId, Fixture.ProductImages.img01());
        final var expectedImage2 = getImage(expectedStore, expectedProductId, Fixture.ProductImages.img02());
        final var expectedImages = Set.of(expectedImage1,expectedImage2);
        // when
        this.storage.storage().putAll(
                expectedImages.stream()
                        .collect(Collectors.toMap(ProductImage::getLocation, Function.identity())));

        // then when
        expectedImages.forEach(img -> {
            Assertions.assertEquals(img, this.storage.get(img.getLocation()).get());
        });
    }

    @Test
    public void givenInvalidParams_whenCallsGet_shouldReturnEmpty() {
        // given
        final var expectedLocation = Fixture.ProductImages.img01().getLocation();


        // when
        final var actualImage = this.storage.get(expectedLocation);

        // then
        Assertions.assertTrue(actualImage.isEmpty());
    }

    @Test
    public void givenValidPrefix_whenCallsList_shouldRetrieveIt() {
        // given
        final var expectedLocations = List.of(
                "image_" + IdUtils.uuid(),
                "image_" + IdUtils.uuid(),
                "image_" + IdUtils.uuid()
        );

        expectedLocations.forEach(name -> this.storage.storage().put(name, Fixture.ProductImages.img01()));

        Assertions.assertEquals(3, this.storage.storage().size());

        // when
        final var actualImages = this.storage.list("image");

        // then
        Assertions.assertTrue(
                expectedLocations.size() == actualImages.size()
                && expectedLocations.containsAll(actualImages)
        );
    }

    @Test
    public void givenValidParams_whenCallsDeleteAll_shouldDelete() {
        // given
        final var images = List.of(
                "image_" + IdUtils.uuid(),
                "image_" + IdUtils.uuid()
        );

        final var expectedLocations = List.of(
                "img_" + IdUtils.uuid(),
                "img_" + IdUtils.uuid(),
                "img_" + IdUtils.uuid()
        );

        final var allImages = new ArrayList<>(images);
        allImages.addAll(expectedLocations);

        allImages.forEach(name -> this.storage.storage().put(name, Fixture.ProductImages.img01()));

        Assertions.assertEquals(5, this.storage.storage().size());

        // when
        this.storage.deleteAll(images);


        // then
        Assertions.assertEquals(3, this.storage.storage().size());

        final var actualKeys = this.storage.storage().keySet();
        Assertions.assertTrue(
                expectedLocations.size() == actualKeys.size()
                        && expectedLocations.containsAll(actualKeys)
        );

    }

    private String getLocation(Store aStore, ProductID aProduct, ProductImage anImage) {
        return "storeId-%s/productId-%s/%s".formatted(aStore.getId(), aProduct.getValue(), anImage.getName());
    }

    private ProductImage getImage(Store aStore, ProductID aProduct, ProductImage anImage) {
        return ProductImage.with(anImage, getLocation(aStore, aProduct, anImage));
    }

}