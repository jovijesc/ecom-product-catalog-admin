package com.ecom.catalog.admin.application.update;

import com.ecom.catalog.admin.application.UseCase;
import com.ecom.catalog.admin.domain.Identifier;
import com.ecom.catalog.admin.domain.exceptions.DomainException;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultUpdateProductUseCase extends UseCase<UpdateProductCommand, UpdateProductOutput> {

    private final ProductGateway productGateway;

    public DefaultUpdateProductUseCase(ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public UpdateProductOutput execute(final UpdateProductCommand aCommand) {
        final var anId = ProductID.from(aCommand.id());
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aStatus = aCommand.status();
        final var aPrice = aCommand.price();
        final var aStock = aCommand.stock();

        final var aProduct = this.productGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var notification = Notification.create();
        notification.validate(() -> aProduct.update(aName, aDescription, aStatus, aPrice, aStock));

        if(notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Product %s".formatted(aCommand.id()), notification);
        }
        return UpdateProductOutput.from(this.productGateway.update(aProduct));
    }

    private Supplier<DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Product.class, anId);
    }
}
