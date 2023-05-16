package com.ecom.catalog.admin.application.product.retrieve.get;

import com.ecom.catalog.admin.application.product.retrieve.get.DefaultGetProductByIdUseCase;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

    @InjectMocks
    private DefaultGetProductByIdUseCase useCase;

    @Mock
    private ProductGateway productGateway;

    @Test
    public void givenAValidId_whenCallsGetProduct_shouldReturnProduct() {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var aProduct =
                Product.newProduct(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId);

        final var expectedId = aProduct.getId();

        when(productGateway.findById(any()))
                .thenReturn(Optional.of(aProduct));

        // when
        final var actualProduct = useCase.execute(expectedId.getValue());

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertEquals(expectedId.getValue(), actualProduct.id());
        Assertions.assertEquals(expectedName, actualProduct.name());
        Assertions.assertEquals(expectedDescription, actualProduct.description());
        Assertions.assertEquals(expectedPrice, actualProduct.price());
        Assertions.assertEquals(expectedStock, actualProduct.stock());
        Assertions.assertEquals(expectedStatus, actualProduct.status());
        Assertions.assertEquals(expectedCategoryId.getValue(), actualProduct.category());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.createdAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), actualProduct.updatedAt());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));

    }

    @Test
    public void givenANonExistentId_whenCallGetProductAndDoesNotExists_shouldReturnNotFound() {
        // given
        final var expectedErrorMessage = "Product with ID 123 was not found";

        final var expectedId = ProductID.from("123");

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.empty());

        // when
        final var actualException =
                Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

}