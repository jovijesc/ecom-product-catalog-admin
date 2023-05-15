package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.AggregateRoot;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.utils.InstantUtils;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.Objects;

public class Product extends AggregateRoot<ProductID> {

    private String name;
    private String description;
    private Money price;
    private int stock;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private Product(
            final ProductID anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final ProductStatus aStatus,
            final Instant aCreationDate,
            final Instant aUpdateDate) {
        super(anId);
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.stock = aStock;
        this.status = aStatus;
        this.createdAt = Objects.requireNonNull(aCreationDate, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(aUpdateDate, "'updatedAt' should not be null");
        selfvalidate();
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock) {
        final var id = ProductID.unique();
        final var now = InstantUtils.now();
        final var DEFAULT_STATUS = ProductStatus.ACTIVE;
        return new Product(id, aName, aDescription, aPrice, aStock, DEFAULT_STATUS, now, now);
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock) {
        final var id = ProductID.unique();
        final var now = InstantUtils.now();
        return new Product(id, aName, aDescription, aPrice, aStock, aStatus, now, now);
    }

    private static Product with(
            final ProductID anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final ProductStatus aStatus,
            final Instant aCreationDate,
            final Instant aUpdateDate) {
        return new Product(
                anId,
                aName,
                aDescription,
                aPrice,
                aStock,
                aStatus,
                aCreationDate,
                aUpdateDate
        );
    }

    public static Product with(final Product aProduct) {
        return with(
                aProduct.getId(),
                aProduct.name,
                aProduct.description,
                aProduct.price,
                aProduct.stock,
                aProduct.status,
                aProduct.createdAt,
                aProduct.updatedAt
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new ProductValidator(this, handler).validate();
    }

    public Product deactivate() {
        return updateStatus(ProductStatus.INACTIVE);
    }

    public Product activate() {
        return updateStatus(ProductStatus.ACTIVE);
    }

    public Product update(final String aName,
                          final String aDescription,
                          final ProductStatus aStatus,
                          final Money aPrice,
                          final int aStock) {
        if(ProductStatus.ACTIVE.equals(aStatus)) {
            activate();
        } else {
            deactivate();
        }
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.stock = aStock;
        this.updatedAt = InstantUtils.now();
        selfvalidate();
        return this;
    }

    private Product updateStatus(ProductStatus active) {
        this.status = active;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    private void selfvalidate() {
        final var notification = Notification.create();
        validate(notification);
        if(notification.hasError()) {
            throw new NotificationException("Failed to create an Aggregate Product", notification);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
