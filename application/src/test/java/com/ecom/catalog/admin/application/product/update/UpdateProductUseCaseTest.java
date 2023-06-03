package com.ecom.catalog.admin.application.product.update;

import com.ecom.catalog.admin.application.product.UseCaseTest;
import com.ecom.catalog.admin.application.product.update.DefaultUpdateProductUseCase;
import com.ecom.catalog.admin.application.product.update.UpdateProductCommand;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


class UpdateProductUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateProductUseCase useCase;
    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private StoreGateway storeGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(productGateway, categoryGateway, storeGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateProduct_shouldReturnProductId() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var aProduct =
                Product.newProduct("Celular A", "Descrição A", ProductStatus.ACTIVE, Money.with(10.0), 10, CategoryID.from("123"), expectedStore);

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedId = aProduct.getId();
        final var expectedCategoryId = CategoryID.from("123");

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStore.getId()
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStore.getId())))
                .thenReturn(true);

        when(productGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStore.getId()));
        Mockito.verify(productGateway).update(Mockito.argThat(aUpdatedProduct ->
                        Objects.equals(expectedId, aUpdatedProduct.getId())
                        && Objects.equals(expectedName, aUpdatedProduct.getName())
                        && Objects.equals(expectedDescription, aUpdatedProduct.getDescription())
                        && Objects.equals(expectedPrice, aUpdatedProduct.getPrice())
                        && Objects.equals(expectedStock, aUpdatedProduct.getStock())
                        && Objects.equals(expectedStatus, aUpdatedProduct.getStatus())
                        && Objects.equals(expectedCategoryId, aUpdatedProduct.getCategoryId())
                        && Objects.equals(expectedStore, aUpdatedProduct.getStore())
                        && Objects.equals(aProduct.getCreatedAt(), aUpdatedProduct.getCreatedAt())
                        && aProduct.getUpdatedAt().isBefore(aUpdatedProduct.getUpdatedAt())
        ));
    }

    @Test
    public void givenAnInvalidNullName_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given

        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, CategoryID.from("123"), expectedStore);

        final var expectedId = aProduct.getId();
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStore.getId()
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStore.getId())))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStore.getId()));
        Mockito.verify(productGateway, times(0)).update(any());

    }

    @Test
    public void givenAnInvalidStock_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, CategoryID.from("123"),expectedStore);

        final var expectedId = aProduct.getId();
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = -1;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'stock' cannot have invalid values";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStore.getId()
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStore.getId())))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStore.getId()));
        Mockito.verify(productGateway, times(0)).update(any());

    }

    @Test
    public void givenANullCategory_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, CategoryID.from("123"), expectedStore);

        final var expectedId = aProduct.getId();
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'category' should not be null";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                null,
                expectedStore.getId()
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(storeGateway.existsById(eq(expectedStore.getId())))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(productGateway, times(0)).update(any());

    }

    @Test
    public void givenAnInvalidCategory_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, CategoryID.from("123"),expectedStore);

        final var expectedId = aProduct.getId();
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("456");

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with ID: 456 could not be found";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStore.getId()
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(false);

        when(storeGateway.existsById(eq(expectedStore.getId())))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStore.getId()));
        Mockito.verify(productGateway, times(0)).update(any());

    }

    @Test
    public void givenAValidCommandWithInactiveProduct_whenCallsUpdateProduct_shouldReturnProductId() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, CategoryID.from("123"), expectedStore);

        final var expectedId = aProduct.getId();
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = CategoryID.from("123");

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStore.getId()
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        when(storeGateway.existsById(eq(expectedStore.getId())))
                .thenReturn(true);

        when(productGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        Assertions.assertEquals(aProduct.getStatus(), ProductStatus.ACTIVE);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStore.getId()));
        Mockito.verify(productGateway, times(1)).update(Mockito.argThat(aUpdatedProduct ->
                Objects.equals(expectedId, aUpdatedProduct.getId())
                        && Objects.equals(expectedName, aUpdatedProduct.getName())
                        && Objects.equals(expectedDescription, aUpdatedProduct.getDescription())
                        && Objects.equals(expectedPrice, aUpdatedProduct.getPrice())
                        && Objects.equals(expectedStock, aUpdatedProduct.getStock())
                        && Objects.equals(expectedStatus, aUpdatedProduct.getStatus())
                        && Objects.equals(expectedCategoryId, aUpdatedProduct.getCategoryId())
                        && Objects.equals(expectedStore, aUpdatedProduct.getStore())
                        && Objects.equals(aProduct.getCreatedAt(), aUpdatedProduct.getCreatedAt())
                        && aProduct.getUpdatedAt().isBefore(aUpdatedProduct.getUpdatedAt())
        ));

    }

    @Test
    public void givenANullStore_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedCategoryId = CategoryID.from("123");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, expectedCategoryId,  Store.with(IdUtils.uuid(), "Minha Loja"));

        final var expectedId = aProduct.getId();
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'store' should not be null";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                null
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(categoryGateway.existsById(eq(expectedCategoryId)))
                .thenReturn(true);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(0)).update(any());

    }

    @Test
    public void givenAnInvalidStore_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedCategoryId = CategoryID.from("123");
        final var aProduct = Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, expectedCategoryId,Store.with("123", "Minha Loja"));
        final var expectedId = aProduct.getId();
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStoreId = "456";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Store with ID: 456 could not be found";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue(),
                expectedStoreId
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

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

        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(storeGateway, times(1)).existsById(eq(expectedStoreId));
        Mockito.verify(productGateway, times(0)).update(any());

    }
}