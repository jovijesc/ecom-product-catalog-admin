package com.ecom.catalog.admin.infrastructure.product.persistence;

import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "Product")
@Table(name = "products")
@TypeDef(typeClass = MonetaryAmountType.class, defaultForType = MonetaryAmount.class)
public class ProductJpaEntity {

    /**
     *     private String name;
     *     private String description;
     *     private Money price;
     *     private int stock;
     *     private ProductStatus status;
     *
     *     private CategoryID categoryId;
     *     private Instant createdAt;
     *     private Instant updatedAt;
     */
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 4000)
    private String description;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Columns(columns = {
            @Column(name = "price_amount", columnDefinition="DECIMAL(17,4)", nullable = false),
            @Column(name = "price_currency", nullable = false)
    })
    private MonetaryAmount price;

    @Column(name = "stock", nullable = false)
    private int stock;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",  nullable = false)
    private CategoryJpaEntity category;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public ProductJpaEntity() {}

    private ProductJpaEntity(
            final String anId,
            final String aName,
            final String aDescription,
            final MonetaryAmount aPrice,
            final int aStock,
            final ProductStatus aStatus,
            final CategoryJpaEntity aCategory,
            final Instant aCreationDate,
            final Instant anUpdateDate
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
    }


    public static ProductJpaEntity from(final Product aProduct) {
        return new ProductJpaEntity(
                aProduct.getId().getValue(),
                aProduct.getName(),
                aProduct.getDescription(),
                fromMoney(aProduct.getPrice()),
                aProduct.getStock(),
                aProduct.getStatus(),
                CategoryJpaEntity.from(aProduct.getCategoryId()),
                aProduct.getCreatedAt(),
                aProduct.getUpdatedAt()
        );
    }

    public Product toAggregate() {
        return Product.with(
                ProductID.from(getId()),
                getName(),
                getDescription(),
                fromMonetaryAmount(getPrice()),
                getStock(),
                getStatus(),
                CategoryID.from(getCategory().getId()),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    private static MonetaryAmount fromMoney(final com.ecom.catalog.admin.domain.product.Money aPrice) {
        return org.javamoney.moneta.Money.of(aPrice.getAmount(), aPrice.getCurrency().getCurrencyCode());
    }

    private static com.ecom.catalog.admin.domain.product.Money fromMonetaryAmount(final MonetaryAmount aPrice) {
        return com.ecom.catalog.admin.domain.product.Money.from(
                aPrice.getNumber().numberValue(BigDecimal.class),
                aPrice.getCurrency().getCurrencyCode()
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
}
