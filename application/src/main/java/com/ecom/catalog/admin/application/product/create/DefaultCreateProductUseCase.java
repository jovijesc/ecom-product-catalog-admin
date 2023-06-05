package com.ecom.catalog.admin.application.product.create;

import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.InternalErrorException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.Set;

public class DefaultCreateProductUseCase extends CreateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    private final StoreGateway storeGateway;

    private ProductImageGateway productImageGateway;

    public DefaultCreateProductUseCase(ProductGateway productGateway, CategoryGateway categoryGateway, StoreGateway storeGateway, ProductImageGateway productImageGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.storeGateway = Objects.requireNonNull(storeGateway);
        this.productImageGateway = Objects.requireNonNull(productImageGateway);
    }

    @Override
    public CreateProductOutput execute(final CreateProductCommand aCommand) {
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aStatus = aCommand.status();
        final var aPrice = aCommand.price();
        final var aStock = aCommand.stock();
        final CategoryID aCategoryId = (Objects.nonNull(aCommand.category())?CategoryID.from(aCommand.category()):null);
        final Store aStore = (Objects.nonNull(aCommand.store())?Store.from(aCommand.store()):null);;
        final Set<ProductImage> images = aCommand.images();

        final var notification = Notification.create();
        notification.append(validateCategory(aCategoryId));
        notification.append(validateStore(aStore));
        final var aProduct = notification.validate(() -> Product.newProduct(aName, aDescription, aStatus, aPrice, aStock, aCategoryId, aStore, images));

        if(notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Product,", notification);
        }
        return CreateProductOutput.from(create(aCommand, aStore, aProduct));
    }

    private Product create(final CreateProductCommand aCommand, final Store aStore, final Product aProduct) {
        final var anId = aProduct.getId();
        try {
            final var images = this.productImageGateway.create(aStore, anId, aCommand.images());
            return this.productGateway.create(aProduct.updateImages(images));
        } catch(final Throwable t) {
            this.productImageGateway.clearImages(aStore, anId);
            throw InternalErrorException.with("An error on create product was observed [productId:%s]".formatted(anId.getValue()),t);
        }
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
