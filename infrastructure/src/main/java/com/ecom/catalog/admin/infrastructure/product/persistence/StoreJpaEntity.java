package com.ecom.catalog.admin.infrastructure.product.persistence;

import com.ecom.catalog.admin.domain.product.Store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Store")
@Table(name = "stores")
public class StoreJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    public StoreJpaEntity() {
    }

    private StoreJpaEntity(final String id) {
        this.id = id;
    }

    private StoreJpaEntity(
            final String id,
            final String name) {
        this.id = id;
        this.name = name;
    }

    public static com.ecom.catalog.admin.infrastructure.product.persistence.StoreJpaEntity from(final Store aStore) {
        return new com.ecom.catalog.admin.infrastructure.product.persistence.StoreJpaEntity(
                aStore.getId(),
                aStore.getName()
        );
    }

    public Store toAggregate() {
        return Store.with(
                getId(),
                getName()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
