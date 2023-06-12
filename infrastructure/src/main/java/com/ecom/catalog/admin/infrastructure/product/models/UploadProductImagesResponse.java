package com.ecom.catalog.admin.infrastructure.product.models;

import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.utils.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record UploadProductImagesResponse(
        @JsonProperty("product") String productId,
        @JsonProperty("ids") Set<String> imagesIds,
        @JsonProperty("uris") Set<String> imagesUris
) {

}

