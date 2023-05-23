package com.ecom.catalog.admin.infrastructure.api.controllers;

import com.ecom.catalog.admin.application.category.create.CreateCategoryCommand;
import com.ecom.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.ecom.catalog.admin.infrastructure.api.CategoryAPI;
import com.ecom.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
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
}
