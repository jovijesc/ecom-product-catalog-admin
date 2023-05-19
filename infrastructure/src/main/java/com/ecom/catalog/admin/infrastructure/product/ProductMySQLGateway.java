package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProductMySQLGateway implements ProductGateway {

    private final ProductRepository productRepository;

    public ProductMySQLGateway(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @Override
    public Product create(final Product aProduct) {
        return null;
    }

    @Override
    public Product activate(final ProductID anId) {
        return null;
    }

    @Override
    public Product inactivate(final ProductID anId) {
        return null;
    }

    @Override
    public Optional<Product> findById(final ProductID anId) {
        return Optional.empty();
    }

    @Override
    public Product update(final Product aProduct) {
        return null;
    }

    @Override
    public Pagination<Product> findAll(final SearchQuery aQuery) {
        return null;
    }

    @Override
    public List<ProductID> existsByIds(final Iterable<ProductID> ids) {
        return null;
    }
}
