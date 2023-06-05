package com.ecom.catalog.admin.application.product.create;

import com.ecom.catalog.admin.application.product.UseCaseTest;
import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.application.product.create.DefaultCreateProductUseCase;
import com.ecom.catalog.admin.application.product.update.UpdateProductCommand;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.InternalErrorException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


class CreateProductUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateProductUseCase useCase;

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private StoreGateway storeGateway;

    @Mock
    private ProductImageGateway productImageGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(productGateway, categoryGateway, storeGateway, productImageGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final var expectedName = "Celular";
        final var expectedStoreId = "123";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);

        when(productImageGateway.create(any(), any(), ArgumentMatchers.anySet()))
                .thenReturn(expectedImages);

        when(productGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productImageGateway, times(1)).create(eq(Store.from(expectedStoreId)), eq(ProductID.from(actualOutput.id())), eq(expectedImages));
        Mockito.verify(productGateway).create(Mockito.argThat(aProduct ->
            Objects.equals(expectedName, aProduct.getName())
                    && Objects.equals(expectedDescription, aProduct.getDescription())
                    && Objects.equals(expectedPrice, aProduct.getPrice())
                    && Objects.equals(expectedStock, aProduct.getStock())
                    && Objects.equals(expectedStatus, aProduct.getStatus())
                    && Objects.nonNull(aProduct.getCategoryId())
                    && Objects.equals(expectedCategoryId, aProduct.getCategoryId())
                    && Objects.equals(expectedStoreId, aProduct.getStore().getId())
                    && Objects.equals(expectedImages, aProduct.getImages())
                    && Objects.nonNull(aProduct.getId())
                    && Objects.nonNull(aProduct.getCreatedAt())
                    && Objects.nonNull(aProduct.getUpdatedAt())
        ));

    }

    @Test
    public void givenAValidCommandWithInactiveProduct_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final var expectedName = "Celular";
        final var expectedStoreId = "123";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);

        when(productImageGateway.create(any(), any(), ArgumentMatchers.anySet()))
                .thenReturn(expectedImages);

        when(productGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productImageGateway, times(1)).create(eq(Store.from(expectedStoreId)), eq(ProductID.from(actualOutput.id())), eq(expectedImages));
        Mockito.verify(productGateway).create(Mockito.argThat(aProduct ->
                Objects.equals(expectedName, aProduct.getName())
                        && Objects.equals(expectedDescription, aProduct.getDescription())
                        && Objects.equals(expectedPrice, aProduct.getPrice())
                        && Objects.equals(expectedStock, aProduct.getStock())
                        && Objects.equals(expectedStatus, aProduct.getStatus())
                        && Objects.nonNull(aProduct.getCategoryId())
                        && Objects.equals(expectedCategoryId, aProduct.getCategoryId())
                        && Objects.equals(expectedStoreId, aProduct.getStore().getId())
                        && Objects.equals(expectedImages, aProduct.getImages())
                        && Objects.nonNull(aProduct.getId())
                        && Objects.nonNull(aProduct.getCreatedAt())
                        && Objects.nonNull(aProduct.getUpdatedAt())

        ));

    }

    @Test
    public void givenAnInvalidNullName_whenCallsCreateProduct_shouldReturnDomainException() {
        // given
        final String expectedName = null;
        final var expectedStoreId = "123";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

    @Test
    public void givenAnInvalidEmptyName_whenCallsCreateProduct_shouldReturnDomainException() {
        // given
        final String expectedName = " ";
        final var expectedStoreId = "123";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);
        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

    @Test
    public void givenAnInvalidCategory_whenCallsCreateProduct_shouldReturnNotificationException() {
        // given
        final var expectedStoreId = "123";
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("456");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with ID: 456 could not be found";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(false);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        // given
        final String expectedName = "Celular";
        final var expectedStoreId = "123";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorMessage = "An error on create product was observed";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);

        when(productImageGateway.create(any(), any(), ArgumentMatchers.anySet()))
                .thenReturn(expectedImages);

        when(productGateway.create(any()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        // when
        final var actualException = Assertions.assertThrows(InternalErrorException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertTrue(actualException.getMessage().startsWith(expectedErrorMessage));

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productImageGateway, times(1)).create(any(), any(), (Set<ProductImage>) any());
        Mockito.verify(productGateway, times(1)).create(any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateProductThrowsException_shouldReturnClearResources() {
        // given
        final String expectedName = "Celular";
        final var expectedStoreId = "123";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorMessage = "An error on create product was observed";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(true);

        when(productImageGateway.create(any(), any(), ArgumentMatchers.anySet()))
                .thenReturn(expectedImages);

        when(productGateway.create(any()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        // when
        final var actualException = Assertions.assertThrows(InternalErrorException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertTrue(actualException.getMessage().startsWith(expectedErrorMessage));

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productImageGateway, times(1)).create(any(), any(), (Set<ProductImage>) any());
        Mockito.verify(productGateway, times(1)).create(any());
        Mockito.verify(productImageGateway).clearImages(any(), any());
    }


    @Test
    public void givenANullStore_whenCallsCreateProduct_shouldReturnNotificationException() {
        // given
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'store' should not be null";

        final var aCommand = CreateProductCommand.with(
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                null,
                expectedImages
        );

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

    @Test
    public void givenAnInvalidStore_whenCallsCreateProduct_shouldReturnNotificationException() {
        // given
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStoreId = "456";
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Store with ID: 456 could not be found";

        final var aCommand = CreateProductCommand.with(
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStoreId,
                expectedImages
        );

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStoreId)))
                .thenReturn(false);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productGateway, times(0)).create(any());

    }

}