package com.ecom.catalog.admin.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductImageResponse(
        @JsonProperty("id") String id,
        @JsonProperty("checksum") String checksum,
        @JsonProperty("name") String name,
        @JsonProperty("location") String location,
        @JsonProperty("featured") boolean featured

) {
}
