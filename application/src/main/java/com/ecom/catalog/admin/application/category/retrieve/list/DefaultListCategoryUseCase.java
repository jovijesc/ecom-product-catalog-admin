package com.ecom.catalog.admin.application.category.retrieve.list;

import com.ecom.catalog.admin.application.product.retrieve.list.ProductListOutput;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListCategoryUseCase extends ListCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<CategoryListOutput> execute(SearchQuery aQuery) {
        return this.categoryGateway.findAll(aQuery)
                .map(CategoryListOutput::from);
    }
}
