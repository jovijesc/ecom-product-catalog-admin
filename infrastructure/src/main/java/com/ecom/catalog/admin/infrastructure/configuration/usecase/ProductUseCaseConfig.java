package com.ecom.catalog.admin.infrastructure.configuration.usecase;

import com.ecom.catalog.admin.application.product.create.CreateProductUseCase;
import com.ecom.catalog.admin.application.product.create.DefaultCreateProductUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.DefaultGetProductByIdUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.GetProductByIdUseCase;
import com.ecom.catalog.admin.application.product.retrieve.list.DefaultListProductUseCase;
import com.ecom.catalog.admin.application.product.retrieve.list.ListProductUseCase;
import com.ecom.catalog.admin.application.product.update.DefaultUpdateProductUseCase;
import com.ecom.catalog.admin.application.product.update.UpdateProductUseCase;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ProductUseCaseConfig {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    public ProductUseCaseConfig(final ProductGateway productGateway, final CategoryGateway categoryGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateProductUseCase createProductUseCase() {
        return new DefaultCreateProductUseCase(productGateway, categoryGateway, storeGateway);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase() {
        return new DefaultUpdateProductUseCase(productGateway, categoryGateway, storeGateway);
    }

    @Bean
    public GetProductByIdUseCase getProductByIdUseCase() {
        return new DefaultGetProductByIdUseCase(productGateway);
    }

    @Bean
    public ListProductUseCase listProductUseCase() {
        return new DefaultListProductUseCase(productGateway);
    }
}
