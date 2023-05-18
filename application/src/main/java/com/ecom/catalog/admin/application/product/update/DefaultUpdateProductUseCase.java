package com.ecom.catalog.admin.application.product.update;

import com.ecom.catalog.admin.application.UseCase;
import com.ecom.catalog.admin.domain.Identifier;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.DomainException;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultUpdateProductUseCase extends UseCase<UpdateProductCommand, UpdateProductOutput> {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    public DefaultUpdateProductUseCase(ProductGateway productGateway, CategoryGateway categoryGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public UpdateProductOutput execute(final UpdateProductCommand aCommand) {
        final var anId = ProductID.from(aCommand.id());
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aStatus = aCommand.status();
        final var aPrice = aCommand.price();
        final var aStock = aCommand.stock();
        final CategoryID aCategoryId = (Objects.nonNull(aCommand.category())?CategoryID.from(aCommand.category()):null);

        final var aProduct = this.productGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var notification = Notification.create();
        notification.append(validateCategory(aCategoryId));
        notification.validate(() -> aProduct.update(aName, aDescription, aStatus, aPrice, aStock, aCategoryId));

        if(notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Product %s".formatted(aCommand.id()), notification);
        }
        return UpdateProductOutput.from(this.productGateway.update(aProduct));
    }

    private Supplier<DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Product.class, anId);
    }

    private Error validateCategory(final CategoryID aCategoryId) {
        if(aCategoryId!=null && !categoryGateway.existsById(aCategoryId)) {
            return new Error("Category with ID: %s could not be found".formatted(Objects.nonNull(aCategoryId)?aCategoryId.getValue():null));
        }
        return null;
    }

}