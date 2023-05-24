package com.ecom.catalog.admin.infrastructure.api.controllers;

import com.ecom.catalog.admin.application.category.create.CreateCategoryCommand;
import com.ecom.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.ecom.catalog.admin.application.category.update.UpdateCategoryCommand;
import com.ecom.catalog.admin.application.category.update.UpdateCategoryUseCase;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.infrastructure.api.CategoryAPI;
import com.ecom.catalog.admin.infrastructure.category.models.CategoryListResponse;
import com.ecom.catalog.admin.infrastructure.category.models.CategoryResponse;
import com.ecom.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import com.ecom.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase, final UpdateCategoryUseCase updateCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCategoryRequest input) {
        final var aCommand = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true
        );
        final var output = this.createCategoryUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);
    }

    @Override
    public Pagination<CategoryListResponse> list(final String search, final int page, final int perPage, final String sort, final String direction) {
        return null;
    }

    @Override
    public CategoryResponse getById(final String id) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateCategoryRequest input) {
        final var aCommand = UpdateCategoryCommand.with(
                id,
                input.name(),
                input.description(),
                input.active()
        );
        final var output = this.updateCategoryUseCase.execute(aCommand);
        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(final String id) {

    }
}
