package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;

public class DefaultCreateProductUseCase extends CreateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    public DefaultCreateProductUseCase(ProductGateway productGateway, CategoryGateway categoryGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CreateProductOutput execute(final CreateProductCommand aCommand) {
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aStatus = aCommand.status();
        final var aPrice = aCommand.price();
        final var aStock = aCommand.stock();
        final CategoryID aCategoryId = (Objects.nonNull(aCommand.category())?CategoryID.from(aCommand.category()):null);

        final var notification = Notification.create();
        notification.append(validateCategory(aCategoryId));
        final var aProduct = notification.validate(() -> Product.newProduct(aName, aDescription, aStatus, aPrice, aStock, aCategoryId));

        if(notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Product,", notification);
        }
        return CreateProductOutput.from(this.productGateway.create(aProduct));
    }

    private Error validateCategory(final CategoryID aCategoryId) {
        if(aCategoryId!=null && !categoryGateway.existsById(aCategoryId)) {
            return new Error("Category with ID: %s could not be found".formatted(Objects.nonNull(aCategoryId)?aCategoryId.getValue():null));
        }
        return null;
    }
}
