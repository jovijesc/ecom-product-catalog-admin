package com.ecom.catalog.admin.application.product.image.get;

import com.ecom.catalog.admin.application.product.UseCaseTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductImageGateway;
import com.ecom.catalog.admin.domain.product.ProductImageID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GetProductImageUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetProductImageUseCase useCase;

    @Mock
    private ProductImageGateway productImageGateway;

    @Mock
    private ProductGateway productGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(productImageGateway, productGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetImage_shouldReturnImage() {
        // given
        final var aProduct = Fixture.Products.celular();
        final var aStore = aProduct.getStore();
        final var expectedImage = aProduct.getImages().stream().findFirst().get();
        final var expectedId = expectedImage.getId();

        when(productGateway.findByImageId(expectedId))
                .thenReturn(Optional.of(aProduct));

        when(productImageGateway.getImage(aStore, aProduct.getId(), expectedImage))
                .thenReturn(Optional.of(expectedImage));

        final var aCommand = GetProductImageCommand.with(aProduct.getId().getValue(), expectedId.getValue());

        // when
        final var actualResult = this.useCase.execute(aCommand);

        // then
        Assertions.assertEquals(expectedImage.getName(), actualResult.name());
        Assertions.assertEquals(expectedImage.getContent(), actualResult.content());

    }

    @Test
    public void givenAnInvalidId_whenCallsGetImage_shouldReturnNotFoundException() {
        // given
        final var aProduct = Fixture.Products.celular();
        final var aStore = aProduct.getStore();
        final var expectedImage = aProduct.getImages().stream().findFirst().get();
        final var expectedId = expectedImage.getId();

        when(productGateway.findByImageId(expectedId))
                .thenReturn(Optional.of(aProduct));

        when(productImageGateway.getImage(aStore, aProduct.getId(), expectedImage))
                .thenReturn(Optional.empty());

        final var aCommand = GetProductImageCommand.with(aProduct.getId().getValue(), expectedId.getValue());

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> this.useCase.execute(aCommand));
    }
}