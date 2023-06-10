package com.ecom.catalog.admin.infrastructure.product.persistence;

import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.utils.CollectionUtils;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "Product")
@Table(name = "products")
@TypeDef(typeClass = MonetaryAmountType.class, defaultForType = MonetaryAmount.class)
public class ProductJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 4000)
    private String description;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Columns(columns = {
            @Column(name = "price_amount", columnDefinition = "DECIMAL(17,4)", nullable = false),
            @Column(name = "price_currency", nullable = false)
    })
    private MonetaryAmount price;

    @Column(name = "stock", nullable = false)
    private int stock;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryJpaEntity category;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private StoreJpaEntity store;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImageJpaEntity> images;


    public ProductJpaEntity() {
    }

    private ProductJpaEntity(
            final String anId,
            final String aName,
            final String aDescription,
            final MonetaryAmount aPrice,
            final int aStock,
            final ProductStatus aStatus,
            final CategoryJpaEntity aCategory,
            final Instant aCreationDate,
            final Instant anUpdateDate,
            final StoreJpaEntity aStore,
            final Set<ProductImageJpaEntity> images
    ) {
        this.id = anId;
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.stock = aStock;
        this.status = aStatus;
        this.category = aCategory;
        this.createdAt = aCreationDate;
        this.updatedAt = anUpdateDate;
        this.store = aStore;
        this.images = images;
    }


    public static ProductJpaEntity from(final Product aProduct) {
        return new ProductJpaEntity(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                MoneyUtils.fromMoney(aProduct.getPrice()),
                aProduct.getStock(),
                aProduct.getStatus(),
                CategoryJpaEntity.from(aProduct.getCategoryId()),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt(),
                StoreJpaEntity.from(aProduct.getStore()),
                CollectionUtils.mapTo(aProduct.getImages(), i -> ProductImageJpaEntity.from(i))
        );
    }

    public Product toAggregate() {
        return Product.with(
                ProductID.from(getId()),
                getName(),
                getDescription(),
                MoneyUtils.fromMonetaryAmount(getPrice()),
                getStock(),
                getStatus(),
                CategoryID.from(getCategory().getId()),
                getCreatedAt(),
                getUpdatedAt(),
                getStore().toAggregate(),
                CollectionUtils.mapTo(getImages(), ProductImageJpaEntity::toDomain)
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MonetaryAmount getPrice() {
        return price;
    }

    public void setPrice(MonetaryAmount price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public CategoryJpaEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryJpaEntity category) {
        this.category = category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StoreJpaEntity getStore() {
        return store;
    }

    public void setStore(StoreJpaEntity store) {
        this.store = store;
    }

    public Set<ProductImageJpaEntity> getImages() {
        return images;
    }

    public void setImages(Set<ProductImageJpaEntity> images) {
        this.images = images;
    }
}
