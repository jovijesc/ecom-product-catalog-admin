package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImageID;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductJpaEntity;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
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
public class DefaultProductGateway implements ProductGateway {

    private final ProductRepository productRepository;

    public DefaultProductGateway(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @Override
    public Product create(final Product aProduct) {
        return save(aProduct);
    }

    @Override
    public Optional<Product> findById(final ProductID anId) {
        return this.productRepository.findById(anId.getValue())
                .map(ProductJpaEntity::toAggregate);
    }

    @Override
    public Optional<Product> findByImageId(final ProductImageID imageId) {
        return this.productRepository.findByImageId(imageId)
                .map(ProductJpaEntity::toAggregate);
    }

    @Override
    public Product update(final Product aProduct) {
        return save(aProduct);
    }

    @Override
    public Pagination<Product> findAll(final SearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var where = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var pageResult = this.productRepository.findAll(where(where), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(ProductJpaEntity::toAggregate).toList()
        );

    }

    @Override
    public List<ProductID> existsByIds(final Iterable<ProductID> productIDS) {
        final var ids = StreamSupport.stream(productIDS.spliterator(), false)
                .map(ProductID::getValue)
                .toList();
        return this.productRepository.existsById(ids).stream()
                .map(ProductID::from)
                .toList();
    }

    private Product save(final Product aProduct) {
        return this.productRepository.save(ProductJpaEntity.from(aProduct))
                .toAggregate();
    }

    private Specification<ProductJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }
}
