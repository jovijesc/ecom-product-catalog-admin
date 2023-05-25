package com.ecom.catalog.admin.infrastructure.product.models;

import com.ecom.catalog.admin.JacksonTest;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.category.models.CategoryResponse;
import org.assertj.core.api.Assertions;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.time.Instant;

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
        final var expectedId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var response = new ProductResponse(
                expectedId,
                expectedName,
                expectedDescription,
                expectedStatus.name(),
                expectedPrice,
                expectedStock,
                expectedCategoryId,
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
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.updated_at", expectedCreatedAt.toString());
    }

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedCategoryId = "123";
        final var expectedId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var json = """
                {
                  "id": "%s",
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
                expectedCreatedAt.toString(),
                expectedUpdatedAt.toString()
        );

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("status", expectedStatus.name())
                .hasFieldOrPropertyWithValue("stock", expectedStock)
                .hasFieldOrPropertyWithValue("price", expectedPrice)
                .hasFieldOrPropertyWithValue("category", expectedCategoryId)
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt);
    }
}

