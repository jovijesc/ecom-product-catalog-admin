package com.ecom.catalog.admin.infrastructure.product.presenters;

import com.ecom.catalog.admin.application.product.retrieve.get.ProductOutput;
import com.ecom.catalog.admin.application.product.retrieve.list.ProductListOutput;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.product.models.ProductListResponse;
import com.ecom.catalog.admin.infrastructure.product.models.ProductResponse;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

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
                output.createdAt(),
                output.updatedAt()
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
                output.createdAt()
        );
    }

}
