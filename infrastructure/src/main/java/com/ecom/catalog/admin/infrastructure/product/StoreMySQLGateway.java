package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductJpaEntity;
import com.ecom.catalog.admin.infrastructure.product.persistence.StoreJpaEntity;
import com.ecom.catalog.admin.infrastructure.product.persistence.StoreRepository;
import com.ecom.catalog.admin.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class StoreMySQLGateway implements StoreGateway {

    private final StoreRepository repository;

    public StoreMySQLGateway(StoreRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Store create(Store aStore) {
        return save(aStore);
    }

    @Override
    public boolean existsById(String id) {
        return this.repository.existsById(id);
    }

    private Store save(final Store aStore) {
        return this.repository.save(StoreJpaEntity.from(aStore))
                .toAggregate();
    }
}
