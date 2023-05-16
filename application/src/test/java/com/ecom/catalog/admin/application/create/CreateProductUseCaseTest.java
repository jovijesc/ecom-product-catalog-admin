package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.application.update.UpdateProductCommand;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @InjectMocks
    private DefaultCreateProductUseCase useCase;

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId.getValue());

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(productGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway).create(Mockito.argThat(aProduct ->
            Objects.equals(expectedName, aProduct.getName())
                    && Objects.equals(expectedDescription, aProduct.getDescription())
                    && Objects.equals(expectedPrice, aProduct.getPrice())
                    && Objects.equals(expectedStock, aProduct.getStock())
                    && Objects.equals(expectedStatus, aProduct.getStatus())
                    && Objects.nonNull(aProduct.getCategoryId())
                    && Objects.equals(expectedCategoryId, aProduct.getCategoryId())
                    && Objects.nonNull(aProduct.getId())
                    && Objects.nonNull(aProduct.getCreatedAt())
                    && Objects.nonNull(aProduct.getUpdatedAt())
        ));

    }

    @Test
    public void givenAValidCommandWithInactiveProduct_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(productGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway).create(Mockito.argThat(aProduct ->
                Objects.equals(expectedName, aProduct.getName())
                        && Objects.equals(expectedDescription, aProduct.getDescription())
                        && Objects.equals(expectedPrice, aProduct.getPrice())
                        && Objects.equals(expectedStock, aProduct.getStock())
                        && Objects.equals(expectedStatus, aProduct.getStatus())
                        && Objects.nonNull(aProduct.getCategoryId())
                        && Objects.equals(expectedCategoryId, aProduct.getCategoryId())
                        && Objects.nonNull(aProduct.getId())
                        && Objects.nonNull(aProduct.getCreatedAt())
                        && Objects.nonNull(aProduct.getUpdatedAt())
        ));

    }

    @Test
    public void givenAnInvalidNullName_whenCallsCreateProduct_shouldReturnDomainException() {
        // given
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

    @Test
    public void givenAnInvalidEmptyName_whenCallsCreateProduct_shouldReturnDomainException() {
        // given
        final String expectedName = " ";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);
        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

    @Test
    public void givenAnInvalidCategory_whenCallsCreateProduct_shouldReturnNotificationException() {
        // given
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, CategoryID.from("123"));

        final var expectedId = aProduct.getId();
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("456");

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with ID: 456 could not be found";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(false);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(0)).update(any());

    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        // given
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var expectedErrorMessage = "Gateway error";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(productGateway.create(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        // when
        final var actualException = Assertions.assertThrows(IllegalStateException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(1)).create(any());
    }


}