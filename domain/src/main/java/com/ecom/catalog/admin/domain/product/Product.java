package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.AggregateRoot;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.utils.InstantUtils;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;
import com.ecom.catalog.admin.domain.validation.handler.Notification;
import com.sun.source.doctree.SeeTree;

import java.time.Instant;
import java.util.*;

public class Product extends AggregateRoot<ProductID> {

    private String name;
    private String description;
    private Money price;
    private int stock;
    private ProductStatus status;

    private CategoryID categoryId;
    private Instant createdAt;
    private Instant updatedAt;
    private Store store;

    private Set<ProductImage> images;

    private Product(
            final ProductID anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final ProductStatus aStatus,
            final CategoryID aCategory,
            final Instant aCreationDate,
            final Instant aUpdateDate,
            final Store aStore,
            final Set<ProductImage> images) {
        super(anId);
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.stock = aStock;
        this.status = aStatus;
        this.categoryId = aCategory;
        this.createdAt = Objects.requireNonNull(aCreationDate, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(aUpdateDate, "'updatedAt' should not be null");
        this.store = aStore;
        this.images = images != null ? images : Collections.emptySet();
        selfValidate();
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final CategoryID aCategoryId,
            final Store aStore,
            final Set<ProductImage> images) {
        final var id = ProductID.unique();
        final var now = InstantUtils.now();
        final var DEFAULT_STATUS = ProductStatus.ACTIVE;
        return new Product(id, aName, aDescription, aPrice, aStock, DEFAULT_STATUS, aCategoryId, now, now, aStore, images);
    }

    public static Product newProduct(
            final String aName,
            final String aDescription,
            final ProductStatus aStatus,
            final Money aPrice,
            final int aStock,
            final CategoryID aCategoryId,
            final Store aStore,
            final Set<ProductImage> images) {
        final var id = ProductID.unique();
        final var now = InstantUtils.now();
        return new Product(id, aName, aDescription, aPrice, aStock, aStatus, aCategoryId, now, now, aStore, images);
    }

    public static Product with(
            final ProductID anId,
            final String aName,
            final String aDescription,
            final Money aPrice,
            final int aStock,
            final ProductStatus aStatus,
            final CategoryID aCategoryId,
            final Instant aCreationDate,
            final Instant aUpdateDate,
            final Store aStore,
            final Set<ProductImage> images) {
        return new Product(
                anId,
                aName,
                aDescription,
                aPrice,
                aStock,
                aStatus,
                aCategoryId,
                aCreationDate,
                aUpdateDate,
                aStore,
                images
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
                aProduct.categoryId,
                aProduct.createdAt,
                aProduct.updatedAt,
                aProduct.store,
                new HashSet<>(aProduct.getImages())
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
                          final int aStock,
                          final CategoryID aCategoryId,
                          final Store aStore,
                          final Set<ProductImage> images) {
        if(ProductStatus.ACTIVE.equals(aStatus)) {
            activate();
        } else {
            deactivate();
        }
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.stock = aStock;
        this.categoryId = aCategoryId;
        this.updatedAt = InstantUtils.now();
        this.store = aStore;
        this.images = new HashSet<>(images != null ? images : Collections.emptySet());
        selfValidate();
        return this;
    }

    public Product updateImages(final Set<ProductImage> images) {
        this.images = new HashSet<>(images != null ? images : Collections.emptySet());
        selfValidate();
        return this;
    }

    private Product updateStatus(ProductStatus active) {
        this.status = active;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    private void selfValidate() {
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

    public CategoryID getCategoryId() {
        return categoryId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Store getStore() {
        return store;
    }

    public Set<ProductImage> getImages() {
        return images != null ? Collections.unmodifiableSet(images) : Collections.emptySet();
    }
}
