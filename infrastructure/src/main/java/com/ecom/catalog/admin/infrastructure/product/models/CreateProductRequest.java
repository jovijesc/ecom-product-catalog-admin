package com.ecom.catalog.admin.infrastructure.product.models;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.money.MonetaryAmount;

public record CreateProductRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        MonetaryAmount price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") String category
) {
}
