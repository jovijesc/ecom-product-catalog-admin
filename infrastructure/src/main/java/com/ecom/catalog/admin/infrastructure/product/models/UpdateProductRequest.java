package com.ecom.catalog.admin.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.money.MonetaryAmount;

public record UpdateProductRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        MonetaryAmount price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") String category
) {
}
