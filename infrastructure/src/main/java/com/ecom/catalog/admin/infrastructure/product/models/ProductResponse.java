package com.ecom.catalog.admin.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.money.MonetaryAmount;
import java.time.Instant;
import java.util.Set;

public record ProductResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("price") MonetaryAmount price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") String category,
        @JsonProperty("store") String store,
        @JsonProperty("images") Set<ProductImageResponse> images,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
}
