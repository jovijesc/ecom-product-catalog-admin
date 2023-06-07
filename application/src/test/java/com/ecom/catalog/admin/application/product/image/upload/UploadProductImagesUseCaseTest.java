package com.ecom.catalog.admin.application.product.image.upload;

import com.ecom.catalog.admin.application.product.UseCaseTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.InternalErrorException;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        final var aProduct = Fixture.Products.celular();
        final var expectedId = aProduct.getId();
        final var expectedProductImages = Set.of(Fixture.ProductImages.img01());

        when(productGateway.findById(any()))
                .thenReturn(Optional.of(aProduct));

        when(productImageGateway.create(any(), any(), (Set<ProductImage>) any()))
                .thenReturn(expectedProductImages);

        when(productGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UploadProductImagesCommand.with(expectedId.getValue(), expectedProductImages);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertEquals(expectedProductImages.size(), actualOutput.imagesIds().size());

        verify(productGateway, times(1)).findById(eq(expectedId));

        verify(productImageGateway, times(1)).create(any(), eq(expectedId), eq(expectedProductImages));

        verify(productGateway, times(1)).update(argThat(actualProduct ->
                Objects.equals(expectedProductImages, actualProduct.getImages())
        ));
    }

    @Test
    public void givenCommandToUpload_whenVideoIsInvalid_shouldReturnNotFound() {
        // given
        final var aProduct = Fixture.Products.celular();
        final var expectedId = aProduct.getId();
        final var expectedProductImages = Set.of(Fixture.ProductImages.img01());

        final var expectedErrorMessage = "Product with ID %s was not found".formatted(expectedId.getValue());

        when(productGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var aCommand = UploadProductImagesCommand.with(expectedId.getValue(), expectedProductImages);

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

    }

    // when(productGateway.create(any()))
    //                .thenThrow(new RuntimeException("Internal Server Error"));

    @Test
    public void givenCommandToUpload_whenProductImageGatewayThrowsRandomException_shouldReturnException() {
        // given
        final var aProduct = Fixture.Products.celular();
        final var expectedId = aProduct.getId();
        final var expectedProductImages = Set.of(Fixture.ProductImages.img01());

        final var expectedErrorMessage = "Internal Server Error";

        when(productGateway.findById(any()))
                .thenReturn(Optional.of(aProduct));

        when(productImageGateway.create(any(), any(), (Set<ProductImage>) any()))
                .thenThrow(new RuntimeException(expectedErrorMessage));

        final var aCommand = UploadProductImagesCommand.with(expectedId.getValue(), expectedProductImages);

        // when
        final var actualException = Assertions.assertThrows(RuntimeException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(productGateway, times(1)).findById(eq(expectedId));
        verify(productImageGateway, times(1)).create(any(), eq(expectedId), eq(expectedProductImages));
        verify(productGateway, times(0)).update(any());

    }
}