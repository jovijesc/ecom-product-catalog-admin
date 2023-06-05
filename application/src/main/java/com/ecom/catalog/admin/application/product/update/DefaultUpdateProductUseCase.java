package com.ecom.catalog.admin.application.product.update;

import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.domain.Identifier;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.DomainException;
import com.ecom.catalog.admin.domain.exceptions.InternalErrorException;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateProductUseCase extends UpdateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    private final StoreGateway storeGateway;

    private final ProductImageGateway productImageGateway;

    public DefaultUpdateProductUseCase(ProductGateway productGateway, CategoryGateway categoryGateway, StoreGateway storeGateway, ProductImageGateway productImageGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.storeGateway = Objects.requireNonNull(storeGateway);
        this.productImageGateway = Objects.requireNonNull(productImageGateway);
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
        final Store aStore = (Objects.nonNull(aCommand.store())?Store.from(aCommand.store()):null);
        final Set<ProductImage> images = aCommand.images();

        final var aProduct = this.productGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var notification = Notification.create();
        notification.append(validateCategory(aCategoryId));
        notification.append(validateStore(aStore));
        notification.validate(() -> aProduct.update(aName, aDescription, aStatus, aPrice, aStock, aCategoryId, aStore, images));

        if(notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Product %s".formatted(aCommand.id()), notification);
        }
        return UpdateProductOutput.from(update(aCommand, aStore, aProduct));
    }

    private Product update(final UpdateProductCommand aCommand, final Store aStore, final Product aProduct) {
        final var anId = aProduct.getId();
        try {
            final var images = this.productImageGateway.create(aStore, anId, aCommand.images());
            return this.productGateway.update(aProduct.updateImages(images));
        } catch(final Throwable t) {
            this.productImageGateway.clearImages(aStore, anId);
            throw InternalErrorException.with("An error on update product was observed [productId:%s]".formatted(anId.getValue()),t);
        }
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

    private Error validateStore(final Store aStore) {
        if(aStore!=null && !storeGateway.existsById(aStore.getId())) {
            return new Error("Store with ID: %s could not be found".formatted(Objects.nonNull(aStore)?aStore.getId():null));
        }
        return null;
    }

}
