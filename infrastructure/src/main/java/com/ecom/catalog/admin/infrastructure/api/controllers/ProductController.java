package com.ecom.catalog.admin.infrastructure.api.controllers;

import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.application.product.create.CreateProductUseCase;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.api.ProductAPI;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.ProductListResponse;
import com.ecom.catalog.admin.infrastructure.product.models.ProductResponse;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class ProductController implements ProductAPI {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(final CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = Objects.requireNonNull(createProductUseCase);
    }

    @Override
    public ResponseEntity<?> create(CreateProductRequest input) {
        final var aCommand = CreateProductCommand.with(
                input.name(),
                input.description(),
                ProductStatus.of(input.status()).orElse(null),
                MoneyUtils.fromMonetaryAmount(input.price()),
                input.stock(),
                input.category()
        );

        final var output = this.createProductUseCase.execute(aCommand);

        return ResponseEntity.created(URI.create("/products/" + output.id())).body(output);
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
