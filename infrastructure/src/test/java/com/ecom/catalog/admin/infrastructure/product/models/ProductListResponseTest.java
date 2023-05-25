package com.ecom.catalog.admin.infrastructure.product.models;

import com.ecom.catalog.admin.JacksonTest;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.assertj.core.api.Assertions;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import javax.money.MonetaryAmount;
import java.time.Instant;

@JacksonTest
public class ProductListResponseTest {

    @Autowired
    private JacksonTester<ProductListResponse> json;

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

        final var response = new ProductListResponse(
                expectedId,
                expectedName,
                expectedDescription,
                expectedStatus.name(),
                expectedPrice,
                expectedStock,
                expectedCategoryId,
                expectedCreatedAt
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
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString());

    }

}
