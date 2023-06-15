package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductImageGateway;
import com.ecom.catalog.admin.infrastructure.services.StorageService;
import com.ecom.catalog.admin.infrastructure.services.impl.AwsS3Service;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        Assertions.assertInstanceOf(AwsS3Service.class, storageService);
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

        Assertions.assertEquals(actualImageStored1.getName(), expectedLocations.get(0));
        Assertions.assertArrayEquals(actualImageStored1.getContent(), expectedImage1.getContent());

        Assertions.assertEquals(actualImageStored2.getName(), expectedLocations.get(1));
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
        Assertions.assertEquals(actualImage.getName(), expectedLocation);
        Assertions.assertArrayEquals(actualImage.getContent(), expectedImageWithLocation.getContent());
        Assertions.assertEquals(actualImage.getLocation(), expectedLocation);
    }
}