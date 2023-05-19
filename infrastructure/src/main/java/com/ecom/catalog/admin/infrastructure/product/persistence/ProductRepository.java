package com.ecom.catalog.admin.infrastructure.product.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductJpaEntity, String> {

    Page<ProductJpaEntity> findAll(Specification<ProductJpaEntity> whereClause, Pageable page);

    @Query(value = "select p.id from Product p where p.id in :ids")
    List<String> existsById(@Param("ids") List<String> ids);
}
