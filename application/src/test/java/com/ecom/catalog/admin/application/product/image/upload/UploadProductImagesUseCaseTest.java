package com.ecom.catalog.admin.application.product.image.upload;

import com.ecom.catalog.admin.application.product.UseCaseTest;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UploadProductImagesUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUploadProductImagesUseCase useCase;

    @Mock
    private ProductImageGateway productImageGateway;

    @Mock
    private ProductGateway productGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(productGateway, productImageGateway);
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateProduct() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));
        final var aProduct =
                Product.newProduct("Celular", "Celular do tipo ABC", ProductStatus.ACTIVE, Money.with(10.0), 10, CategoryID.from("123"), expectedStore, expectedImages);
        // when

        // then
    }
}