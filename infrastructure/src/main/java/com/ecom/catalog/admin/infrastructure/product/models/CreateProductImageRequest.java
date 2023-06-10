package com.ecom.catalog.admin.infrastructure.product.models;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;

public record CreateProductImageRequest(
        MultipartFile image,
        int order,
        boolean featured
) {
}
