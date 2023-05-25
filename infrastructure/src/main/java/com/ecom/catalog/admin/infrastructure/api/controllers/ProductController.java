package com.ecom.catalog.admin.infrastructure.api.controllers;

import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.infrastructure.api.ProductAPI;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.ProductListResponse;
import com.ecom.catalog.admin.infrastructure.product.models.ProductResponse;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductAPI {

    @Override
    public ResponseEntity<?> create(CreateProductRequest input) {
        return null;
    }

    @Override
    public Pagination<ProductListResponse> list(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

    @Override
    public ProductResponse getById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateById(String id, UpdateProductRequest input) {
        return null;
    }
}
