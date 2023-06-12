package com.ecom.catalog.admin.infrastructure.api.controllers;

import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.application.product.create.CreateProductUseCase;
import com.ecom.catalog.admin.application.product.image.get.GetProductImageCommand;
import com.ecom.catalog.admin.application.product.image.get.GetProductImageUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.GetProductByIdUseCase;
import com.ecom.catalog.admin.application.product.retrieve.list.ListProductUseCase;
import com.ecom.catalog.admin.application.product.update.UpdateProductCommand;
import com.ecom.catalog.admin.application.product.update.UpdateProductUseCase;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.api.ProductAPI;
import com.ecom.catalog.admin.infrastructure.product.models.*;
import com.ecom.catalog.admin.infrastructure.product.presenters.ProductApiPresenter;
import com.ecom.catalog.admin.infrastructure.utils.HashingUtils;
import com.ecom.catalog.admin.infrastructure.utils.ImageTypeUtils;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
public class ProductController implements ProductAPI {

    private final CreateProductUseCase createProductUseCase;

    private final UpdateProductUseCase updateProductUseCase;

    private final GetProductByIdUseCase getProductByIdUseCase;

    private final ListProductUseCase listProductUseCase;

    private final GetProductImageUseCase getProductImageUseCase;

    public ProductController(
            final CreateProductUseCase createProductUseCase,
            final UpdateProductUseCase updateProductUseCase,
            final GetProductByIdUseCase getProductByIdUseCase,
            final ListProductUseCase listProductUseCase,
            final GetProductImageUseCase getProductImageUseCase) {
        this.createProductUseCase = Objects.requireNonNull(createProductUseCase);
        this.updateProductUseCase = Objects.requireNonNull(updateProductUseCase);
        this.getProductByIdUseCase = Objects.requireNonNull(getProductByIdUseCase);
        this.listProductUseCase = Objects.requireNonNull(listProductUseCase);
        this.getProductImageUseCase = Objects.requireNonNull(getProductImageUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateProductRequest product, final MultipartFile[] images) {
        final var aCommand = CreateProductCommand.with(
                product.name(),
                product.description(),
                ProductStatus.of(product.status()).orElse(null),
                MoneyUtils.fromMonetaryAmount(product.price()),
                product.stock(),
                product.category(),
                product.store(),
                imagesOf(images, product.numberImageMarkedAsFeatured())
        );

        final var output = this.createProductUseCase.execute(aCommand);

        return ResponseEntity.created(URI.create("/products/" + output.id())).body(output);
    }

    @Override
    public Pagination<ProductListResponse> list(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String direction) {
            return this.listProductUseCase.execute(new SearchQuery(page, perPage, search, sort, direction))
                    .map(ProductApiPresenter::present);
    }

    @Override
    public ProductResponse getById(final String id) {
        return ProductApiPresenter.present(this.getProductByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateProductRequest input) {
        final var aCommand = UpdateProductCommand.with(
                id,
                input.name(),
                input.description(),
                ProductStatus.of(input.status()).orElse(null),
                MoneyUtils.fromMonetaryAmount(input.price()),
                input.stock(),
                input.category(),
                input.store(),
                null
        );
        final var output = this.updateProductUseCase.execute(aCommand);
        return ResponseEntity.ok(output);
    }

    @Override
    public ResponseEntity<byte[]> getImageById(final String id, final String idImage) {
        final var aImage = this.getProductImageUseCase.execute(GetProductImageCommand.with(id, idImage));
        return ResponseEntity.ok()
                .contentType(ImageTypeUtils.getMediaType(aImage.name()))
                .contentLength(aImage.content().length)
                .header(CONTENT_DISPOSITION, "attachment; filename=%s".formatted(aImage.name()))
                .body(aImage.content());
    }

    @Override
    public ResponseEntity<?> uploadImage(final String id, final MultipartFile image) {
        return null;
    }

    @Override
    public void deleteImageById(final String id, final String idImage) {

    }

    private Set<ProductImage> imagesOf(final MultipartFile[] images, final int imageMarkedAsFeatured) {
        if( images == null ) {
            return null;
        }
        try {
            return IntStream.range(0, images.length)
                    .mapToObj(i -> imageOf(images[i], i == imageMarkedAsFeatured))
                    .peek(System.out::println)
                    .collect(Collectors.toSet());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private ProductImage imageOf(final MultipartFile img, final boolean isFeatured) {
        try {
            return ProductImage.with(
                    HashingUtils.checksum(img.getBytes()),
                    img.getBytes(),
                    img.getOriginalFilename(),
                    isFeatured
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
