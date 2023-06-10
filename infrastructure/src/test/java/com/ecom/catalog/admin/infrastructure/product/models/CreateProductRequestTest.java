package com.ecom.catalog.admin.infrastructure.product.models;

import com.ecom.catalog.admin.JacksonTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import org.assertj.core.api.Assertions;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

@JacksonTest
public class CreateProductRequestTest {

    @Autowired
    private JacksonTester<CreateProductRequest> json;

    @Test
    public void testMarshall() throws Exception {
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedCategoryId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStoreId = "123";
        final var expectedImageMarkedAsFeatured = 1;

        final var request = new CreateProductRequest(
                expectedName,
                expectedDescription,
                expectedStatus.name(),
                expectedPrice,
                expectedStock,
                expectedCategoryId,
                expectedStoreId,
                expectedImageMarkedAsFeatured
        );

        final var actualJson = this.json.write(request);

        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.status", expectedStatus.name())
                .hasJsonPathValue("$.stock", expectedStock)
                .hasJsonPathValue("$.price.amount", expectedPrice.getNumber().toString())
                .hasJsonPathValue("$.price.currency", expectedPrice.getCurrency().getCurrencyCode())
                .hasJsonPathValue("$.category", expectedCategoryId)
                .hasJsonPathValue("$.store", expectedCategoryId)
                .hasJsonPathValue("$.number_image_marked_featured", expectedImageMarkedAsFeatured);
    }

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedCategoryId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStoreId = "123";
        final var expectedImageMarkedAsFeatured = 1;

        final var json = """
                {                  
                  "name": "%s",
                  "description": "%s",
                  "status": "%s",
                  "stock": "%s",
                  "price": 
                  {
                       "amount": "%s",
                       "currency": "%s"
                  },
                  "category": "%s",
                  "store": "%s",
                  "number_image_marked_featured": "%s"
                }    
                """.formatted(
                expectedName,
                expectedDescription,
                expectedStatus.name(),
                expectedStock,
                expectedPrice.getNumber().toString(),
                expectedPrice.getCurrency().getCurrencyCode(),
                expectedCategoryId,
                expectedStoreId,
                expectedImageMarkedAsFeatured
        );

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("status", expectedStatus.name())
                .hasFieldOrPropertyWithValue("stock", expectedStock)
                .hasFieldOrPropertyWithValue("price", expectedPrice)
                .hasFieldOrPropertyWithValue("category", expectedCategoryId)
                .hasFieldOrPropertyWithValue("store", expectedStoreId);
    }
}
