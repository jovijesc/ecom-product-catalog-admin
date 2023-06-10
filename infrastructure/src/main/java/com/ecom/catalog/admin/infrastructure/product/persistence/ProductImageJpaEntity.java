package com.ecom.catalog.admin.infrastructure.product.persistence;

import com.ecom.catalog.admin.domain.product.ProductImage;

import javax.persistence.*;

@Entity(name = "ProductImage")
@Table(name = "products_image")
public class ProductImageJpaEntity {

    @Id
    private String id;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "featured", nullable = false)
    private boolean featured;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    public ProductImageJpaEntity() {
    }

    private ProductImageJpaEntity(
            final String id,
            final String checksum,
            final String name,
            final String filePath,
            final boolean featured) {
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.filePath = filePath;
        this.featured = featured;
    }

    public static ProductImageJpaEntity from(final ProductImage image) {
        return new ProductImageJpaEntity(
                image.getId().getValue(),
                image.getChecksum(),
                image.getName(),
                image.getLocation(),
                image.isFeatured()
        );
    }

    public ProductImage toDomain() {
        return ProductImage.with(
                getId(),
                getChecksum(),
                getName(),
                getFilePath(),
                isFeatured()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public ProductJpaEntity getProduct() {
        return product;
    }

    public void setProduct(ProductJpaEntity product) {
        this.product = product;
    }
}
