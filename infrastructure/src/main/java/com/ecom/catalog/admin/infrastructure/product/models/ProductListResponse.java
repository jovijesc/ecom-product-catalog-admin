package com.ecom.catalog.admin.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.money.MonetaryAmount;
import java.time.Instant;

public record ProductListResponse(

        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        MonetaryAmount price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") String category,
        @JsonProperty("created_at") Instant createdAt,

        @JsonProperty("store") String store
        ) {
}
