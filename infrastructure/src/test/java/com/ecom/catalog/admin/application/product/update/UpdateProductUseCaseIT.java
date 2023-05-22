package com.ecom.catalog.admin.application.product.update;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.application.product.create.DefaultCreateProductUseCase;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductJpaEntity;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@IntegrationTest
public class UpdateProductUseCaseIT {

    @Autowired
    private DefaultUpdateProductUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private ProductGateway productGateway;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void givenAValidCommand_whenCallsUpdateProduct_shouldReturnProductId() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var aProduct =
                productGateway.create(Product.newProduct("Celular A", "Descrição A", ProductStatus.ACTIVE, Money.with(10.0), 10, expectedCategory.getId()));

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedId = aProduct.getId();
        final var expectedCategoryId = expectedCategory.getId();

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue()
        );

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualProduct = productRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, ProductJpaEntity.fromMonetaryAmount(actualProduct.getPrice()));
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategory().toAggregate().getId());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertTrue(aProduct.getUpdatedAt().isBefore(actualProduct.getUpdatedAt()));
    }

    @Test
    public void givenAnInvalidNullName_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var aProduct =
                productGateway.create(Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, expectedCategory.getId()));

        final var expectedId = aProduct.getId();
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue()
        );

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
    public void givenAnInvalidCategory_whenCallsUpdateProduct_shouldReturnNotificationException() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var aProduct =
                productGateway.create(Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, expectedCategory.getId()));

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
                expectedCategoryId.getValue()
        );

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
    public void givenAValidCommandWithInactiveProduct_whenCallsUpdateProduct_shouldReturnProductId() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var aProduct =
                productGateway.create(Product.newProduct("Celular A", "Celular do tipo BBB", Money.with(1700.0), 10, expectedCategory.getId()));


        final var expectedId = aProduct.getId();
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = expectedCategory.getId();

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock,
                expectedCategoryId.getValue()
        );


        Assertions.assertEquals(aProduct.getStatus(), ProductStatus.ACTIVE);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);

        final var actualProduct = productRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, ProductJpaEntity.fromMonetaryAmount(actualProduct.getPrice()));
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategory().toAggregate().getId());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertTrue(aProduct.getUpdatedAt().isBefore(actualProduct.getUpdatedAt()));


    }

}


