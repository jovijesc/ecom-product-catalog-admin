package com.ecom.catalog.admin.application.product.retrieve.list;

import com.ecom.catalog.admin.application.UseCase;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;

public abstract class ListProductUseCase
    extends UseCase<SearchQuery, Pagination<ProductListOutput>> {
}
