package com.ecom.catalog.admin.application.category.retrieve.list;

import com.ecom.catalog.admin.application.UseCase;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;

public abstract class ListCategoryUseCase
        extends UseCase<SearchQuery, Pagination<CategoryListOutput>> {
}
