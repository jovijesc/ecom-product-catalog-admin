package com.ecom.catalog.admin.infrastructure.configuration.usecase;

import com.ecom.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.ecom.catalog.admin.application.category.create.DefaultCreateCategoryUseCase;
import com.ecom.catalog.admin.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.ecom.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.ecom.catalog.admin.application.category.retrieve.list.DefaultListCategoryUseCase;
import com.ecom.catalog.admin.application.category.retrieve.list.ListCategoryUseCase;
import com.ecom.catalog.admin.application.category.update.DefaultUpdateCategoryUseCase;
import com.ecom.catalog.admin.application.category.update.UpdateCategoryUseCase;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public ListCategoryUseCase listCategoryUseCase() {
        return new DefaultListCategoryUseCase(categoryGateway);
    }
}
