package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;

public class DefaultCreateProductUseCase extends CreateProductUseCase {

    private final ProductGateway productGateway;

    public DefaultCreateProductUseCase(ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public CreateProductOutput execute(final CreateProductCommand aCommand) {
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aPrice = aCommand.price();
        final var aStock = aCommand.stock();

        final var notification = Notification.create();
        final var aProduct = notification.validate(() -> Product.newProduct(aName, aDescription, aPrice, aStock));

        if(notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Product,", notification);
        }
        return CreateProductOutput.from(this.productGateway.create(aProduct));
    }
}
