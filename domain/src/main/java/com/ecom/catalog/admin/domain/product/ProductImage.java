package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.Entity;
import com.ecom.catalog.admin.domain.ValueObject;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;
import com.ecom.catalog.admin.domain.validation.handler.Notification;

import java.util.Arrays;
import java.util.Objects;

public class ProductImage extends Entity<ProductImageID> {

    private final String checksum;
    private final byte[] content;
    private final String name;
    private final String location;
    private final boolean featured;

    private ProductImage(final ProductImageID anId, final String aChecksum, final byte[] aContent,  String aName, final String aLocation, final boolean aFeatured) {
        super(anId);
        this.checksum = aChecksum;
        this.content = aContent;
        this.name = aName;
        this.location = aLocation;
        this.featured = aFeatured;
        selfValidate();
    }

    private ProductImage(final ProductImageID anId, final String aChecksum, String aName, final String aLocation, final boolean aFeatured) {
        super(anId);
        this.checksum = aChecksum;
        this.content = null;
        this.name = aName;
        this.location = aLocation;
        this.featured = aFeatured;
        selfValidate();
    }

    public static ProductImage with(final String aChecksum, final byte[] aContent, final String aName, final String aLocation, final boolean aFeatured) {
        return new ProductImage(ProductImageID.unique(), aChecksum, aContent, aName, aLocation, aFeatured);
    }

    public static ProductImage with(final String aChecksum, final  String aName, final String aLocation, final boolean aFeatured) {
        return new ProductImage(ProductImageID.unique(), aChecksum, aName, aLocation, aFeatured);
    }

    public static ProductImage with(final String anId, final String aChecksum, final String aName, final String aLocation, final boolean aFeatured) {
        return new ProductImage(ProductImageID.from(anId), aChecksum, aName, aLocation, aFeatured);
    }

    public static ProductImage with(final String aChecksum, final byte[] aContent, final String aName, final boolean aFeatured) {
        return new ProductImage(ProductImageID.unique(), aChecksum, aContent, aName, "location", aFeatured);
    }

    public static ProductImage with(final ProductImage aImage) {
        return new ProductImage(aImage.getId(), aImage.getChecksum(), aImage.getContent(), aImage.getName(), aImage.getLocation(), aImage.isFeatured());
    }

    public void validate(ValidationHandler handler) {
        new ProductImageValidator(this, handler).validate();
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);
        if(notification.hasError()) {
            throw new NotificationException("Failed to create an entity ProductImage", notification);
        }
    }

    public String getChecksum() {
        return checksum;
    }

    public byte[] getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public boolean isFeatured() {
        return featured;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductImage that = (ProductImage) o;
        return getChecksum().equals(that.getChecksum()) && getLocation().equals(that.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChecksum(), getLocation());
    }

}
