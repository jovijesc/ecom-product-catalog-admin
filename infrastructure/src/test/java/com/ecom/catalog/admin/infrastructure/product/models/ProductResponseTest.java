package com.ecom.catalog.admin.infrastructure.product.models;

import com.ecom.catalog.admin.JacksonTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.domain.utils.CollectionUtils;
import com.ecom.catalog.admin.infrastructure.product.presenters.ProductApiPresenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.javamoney.moneta.Money;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JacksonTest
public class ProductResponseTest {
    @Autowired
    private JacksonTester<ProductResponse> json;

    @Test
    public void testMarshall() throws Exception {
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedCategoryId = "123";
        final var expectedStoreId = "123";
        final var expectedId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        final var expectedImages = CollectionUtils.mapTo(
                Set.of(Fixture.ProductImages.img01(), Fixture.ProductImages.img02()),
                image -> ProductApiPresenter.present(image));

        final var response = new ProductResponse(
                expectedId,
                expectedName,
                expectedDescription,
                expectedStatus.name(),
                expectedPrice,
                expectedStock,
                expectedCategoryId,
                expectedStoreId,
                expectedImages,
                expectedCreatedAt,
                expectedUpdatedAt
        );

        final var actualJson = this.json.write(response);

        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.status", expectedStatus.name())
                .hasJsonPathValue("$.stock", expectedStock)
                .hasJsonPathValue("$.price.amount", expectedPrice.getNumber().toString())
                .hasJsonPathValue("$.price.currency", expectedPrice.getCurrency().getCurrencyCode())
                .hasJsonPathValue("$.category", expectedCategoryId)
                .hasJsonPathValue("$.store", expectedStoreId)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.updated_at", expectedCreatedAt.toString());

        for (int i = 0; i < expectedImages.size(); i++) {

            final var expectedImage = (ProductImageResponse) expectedImages.toArray()[i];
            final var imageJsonPath = String.format("$.images[%d]", i);

            Assertions.assertThat(actualJson)
                    .hasJsonPathValue(imageJsonPath + ".id", expectedImage.id())
                    .hasJsonPathValue(imageJsonPath + ".checksum", expectedImage.checksum())
                    .hasJsonPathValue(imageJsonPath + ".name", expectedImage.name())
                    .hasJsonPathValue(imageJsonPath + ".location", expectedImage.location())
                    .hasJsonPathValue(imageJsonPath + ".featured", expectedImage.featured());
        }

    }

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedCategoryId = "123";
        final var expectedStoreId = "123";
        final var expectedId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        final var expectedImages = CollectionUtils.mapTo(
                Set.of(Fixture.ProductImages.img01(), Fixture.ProductImages.img02()),
                image -> ProductApiPresenter.present(image));

        final var imagesJson = expectedImages.stream()
                .map(image -> String.format(
                        "{ \"id\": \"%s\", \"checksum\": \"%s\", \"name\": \"%s\", \"location\": \"%s\", \"featured\": %s }",
                        image.id(), image.checksum(), image.name(), image.location(), image.featured()))
                .collect(Collectors.joining(","));

        final var json = """
                {
                  "id": "%s",
                  "name": "%s",
                  "description": "%s",
                  "status": "%s",
                  "stock": %s,
                  "price": {
                    "amount": "%s",
                    "currency": "%s"
                  },
                  "category": "%s",
                  "store": "%s",
                  "images": [%s],
                  "created_at": "%s",
                  "updated_at": "%s"
                }
                """.formatted(
                expectedId,
                expectedName,
                expectedDescription,
                expectedStatus.name(),
                expectedStock,
                expectedPrice.getNumber().toString(),
                expectedPrice.getCurrency().getCurrencyCode(),
                expectedCategoryId,
                expectedStoreId,
                imagesJson,
                expectedCreatedAt.toString(),
                expectedUpdatedAt.toString()
        );

        final var actualJson = this.json.parse(json);

        var actualAssertions = Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("status", expectedStatus.name())
                .hasFieldOrPropertyWithValue("stock", expectedStock)
                .hasFieldOrPropertyWithValue("price", expectedPrice)
                .hasFieldOrPropertyWithValue("category", expectedCategoryId)
                .hasFieldOrPropertyWithValue("store", expectedStoreId)
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt)
                .hasFieldOrPropertyWithValue("images", expectedImages);

    }
}

