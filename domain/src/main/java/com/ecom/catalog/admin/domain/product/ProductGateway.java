package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface ProductGateway {

    Product create(Product aProduct);

    Optional<Product> findById(ProductID anId);

    Optional<Product> findByImageId(ProductImageID imageId);

    Product update(Product aProduct);

    Pagination<Product> findAll(SearchQuery aQuery);

    List<ProductID> existsByIds(Iterable<ProductID> ids);


}
