package com.ecom.catalog.admin.application.product.retrieve.get;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.product.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@IntegrationTest
public class GetProductByIdUseCaseIT {

    @Autowired
    private DefaultGetProductByIdUseCase useCase;

    @SpyBean
    private ProductGateway productGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidId_whenCallsGetProduct_shouldReturnProduct() {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;

        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));
        final var expectedCategoryId = expectedCategory.getId();
        // TODO continuar
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var aProduct =
                productGateway.create(Product.newProduct(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        final var expectedId = aProduct.getId();

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

    }

    @Test
    public void givenANonExistentId_whenCallGetProductAndDoesNotExists_shouldReturnNotFound() {
        // given
        final var expectedErrorMessage = "Product with ID 123 was not found";

        final var expectedId = ProductID.from("123");

        // when
        final var actualException =
                Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        Mockito.verify(productGateway, times(1)).findById(eq(expectedId));
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

}
