package com.ecom.catalog.admin.infrastructure.api.controllers;

import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.application.product.create.CreateProductUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.GetProductByIdUseCase;
import com.ecom.catalog.admin.application.product.update.UpdateProductCommand;
import com.ecom.catalog.admin.application.product.update.UpdateProductUseCase;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.api.ProductAPI;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.ProductListResponse;
import com.ecom.catalog.admin.infrastructure.product.models.ProductResponse;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.presenters.ProductApiPresenter;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class ProductController implements ProductAPI {

    private final CreateProductUseCase createProductUseCase;

    private final UpdateProductUseCase updateProductUseCase;

    private final GetProductByIdUseCase getProductByIdUseCase;

    public ProductController(
            final CreateProductUseCase createProductUseCase,
            final UpdateProductUseCase updateProductUseCase,
            final GetProductByIdUseCase getProductByIdUseCase) {
        this.createProductUseCase = Objects.requireNonNull(createProductUseCase);
        this.updateProductUseCase = Objects.requireNonNull(updateProductUseCase);
        this.getProductByIdUseCase = Objects.requireNonNull(getProductByIdUseCase);
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
        return ProductApiPresenter.present(this.getProductByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(String id, UpdateProductRequest input) {
        final var aCommand = UpdateProductCommand.with(
                id,
                input.name(),
                input.description(),
                ProductStatus.of(input.status()).orElse(null),
                MoneyUtils.fromMonetaryAmount(input.price()),
                input.stock(),
                input.category()
        );
        final var output = this.updateProductUseCase.execute(aCommand);
        return ResponseEntity.ok(output);
    }
}
