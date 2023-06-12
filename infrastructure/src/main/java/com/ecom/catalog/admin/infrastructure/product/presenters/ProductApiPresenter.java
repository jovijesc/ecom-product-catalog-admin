package com.ecom.catalog.admin.infrastructure.product.presenters;

import com.ecom.catalog.admin.application.product.image.upload.UploadProductImagesOutput;
import com.ecom.catalog.admin.application.product.retrieve.get.ProductOutput;
import com.ecom.catalog.admin.application.product.retrieve.list.ProductListOutput;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.domain.utils.CollectionUtils;
import com.ecom.catalog.admin.infrastructure.product.models.ProductImageResponse;
import com.ecom.catalog.admin.infrastructure.product.models.ProductListResponse;
import com.ecom.catalog.admin.infrastructure.product.models.ProductResponse;
import com.ecom.catalog.admin.infrastructure.product.models.UploadProductImagesResponse;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.SimpleIdGenerator;

import javax.money.MonetaryAmount;
import java.time.Instant;

public interface ProductApiPresenter {

    static ProductResponse present(final ProductOutput output) {
        return new ProductResponse(
                output.id(),
                output.name(),
                output.description(),
                output.status().name(),
                MoneyUtils.fromMoney(output.price()),
                output.stock(),
                output.category(),
                output.store(),
                CollectionUtils.mapTo(output.images(), image -> present(image)),
                output.createdAt(),
                output.updatedAt()
        );
    }

    static ProductImageResponse present(final ProductImage image) {
        if (image == null) {
            return null;
        }
        return new ProductImageResponse (
                image.getId().getValue(),
                image.getChecksum(),
                image.getName(),
                image.getLocation(),
                image.isFeatured()
        );
    }

    static ProductListResponse present(final ProductListOutput output) {
        return new ProductListResponse(
                output.id(),
                output.name(),
                output.description(),
                output.status().name(),
                MoneyUtils.fromMoney(output.price()),
                output.stock(),
                output.category(),
                output.createdAt(),
                output.store()
        );
    }

    static UploadProductImagesResponse present(final UploadProductImagesOutput output, String baseUri) {
        return new UploadProductImagesResponse(
                output.productId(),
                output.imagesIds(),
                CollectionUtils.mapTo(output.imagesIds(), img -> baseUri.formatted(output.productId(), img))
        );
    }
}
