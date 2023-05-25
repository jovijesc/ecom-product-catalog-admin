package com.ecom.catalog.admin.application.product.create;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.application.category.create.DefaultCreateCategoryUseCase;
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
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@IntegrationTest
public class CreateProductUseCaseIT {

    @Autowired
    private DefaultCreateProductUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private ProductGateway productGateway;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void givenAValidCommand_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId.getValue());

        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualProduct = productRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, MoneyUtils.fromMonetaryAmount(actualProduct.getPrice()));
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategory().toAggregate().getId());
        Assertions.assertNotNull(actualProduct.getCreatedAt());
        Assertions.assertNotNull(actualProduct.getUpdatedAt());

    }

    @Test
    public void givenAnInvalidNullName_whenCallsCreateProduct_shouldReturnDomainException() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

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
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();

        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(productGateway).create(any());

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue());

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
