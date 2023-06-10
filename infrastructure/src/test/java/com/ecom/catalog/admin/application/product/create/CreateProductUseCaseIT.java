package com.ecom.catalog.admin.application.product.create;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.application.category.create.DefaultCreateCategoryUseCase;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.InternalErrorException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductJpaEntity;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import com.ecom.catalog.admin.infrastructure.product.persistence.StoreRepository;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;
import java.util.Set;

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

    @SpyBean
    private StoreGateway storeGateway;

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void givenAValidCommand_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final var expectedCategory =
                categoryGateway.create(Fixture.Categories.eletronicos());
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStore.getId(), expectedImages);

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
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());

        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();
        final var expectedStoreId = expectedStore.getId();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

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
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());

        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory.getId();
        final var expectedStoreId = expectedStore.getId();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());


        final var expectedErrorMessage = "An error on create product was observed";

        doThrow(InternalErrorException.with(expectedErrorMessage))
                .when(productGateway).create(any());

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStoreId, expectedImages);

        // when
        final var actualException = Assertions.assertThrows(InternalErrorException.class, () ->
                useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertTrue(actualException.getMessage().startsWith(expectedErrorMessage));

        Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategoryId));
        Mockito.verify(productGateway, times(1)).create(any());
    }

}
