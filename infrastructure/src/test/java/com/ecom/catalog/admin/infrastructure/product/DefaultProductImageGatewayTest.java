package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.product.ProductID;
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

import java.util.Set;

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
        final var expectedImages = Set.of(Fixture.ProductImages.img01(),Fixture.ProductImages.img02());


        // when
        final var actualImage = this.productImageGateway.create(expectedStore, expectedProductId, expectedImages);

        // then

    }
}