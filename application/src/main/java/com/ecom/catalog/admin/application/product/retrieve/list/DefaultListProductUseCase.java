package com.ecom.catalog.admin.application.product.retrieve.list;

import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.ProductGateway;

import java.util.Objects;

public class DefaultListProductUseCase extends ListProductUseCase {

    private final ProductGateway productGateway;

    public DefaultListProductUseCase(ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public Pagination<ProductListOutput> execute(SearchQuery aQuery) {
        return this.productGateway.findAll(aQuery)
                .map(ProductListOutput::from);
    }
}
