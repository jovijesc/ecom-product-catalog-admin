package com.ecom.catalog.admin.application.product.retrieve.list;

import com.ecom.catalog.admin.IntegrationTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryGateway;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

@IntegrationTest
public class ListProductUseCaseIT {

    @Autowired
    private DefaultListProductUseCase useCase;

    @SpyBean
    private ProductGateway productGateway;

    @SpyBean
    private CategoryGateway categoryGateway;


    @Test
    public void givenAValidQuery_whenCallsListProduct_shouldReturnProducts() {
        // given
        final var expectedCategoryOne =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));
        final var expectedCategoryTwo =
                categoryGateway.create(Category.newCategory("Informática", "Informática do tipo A", true));

        // TODO continuar
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var products = List.of(
                productGateway.create(Product.newProduct("Celular", "Celular do tipo ABC", ProductStatus.ACTIVE, Money.with(3000.0), 10, expectedCategoryOne.getId(), expectedStore, expectedImages)),
                productGateway.create(Product.newProduct("Notebook", "Notebook do tipo 123", ProductStatus.ACTIVE, Money.with(5000.0), 10, expectedCategoryTwo.getId(), expectedStore, expectedImages))
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "e";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedItems = products.stream()
                .map(ProductListOutput::from)
                .toList();

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());
        Assertions.assertTrue(
                expectedItems.size() == actualOutput.items().size()
                        && expectedItems.containsAll(actualOutput.items())
        );

    }

    @Test
    public void givenAValidQuery_whenCallsListProductAndResultIsEmpty_shouldReturnProducts() {
        // given

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = List.<ProductListOutput>of();

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

    }

    @Test
    public void givenAValidQuery_whenCallsListProductAndGatewayThrowsRandomError_shouldReturnException() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(productGateway).findAll(any());

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(aQuery)
        );

        // then
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());

        Mockito.verify(productGateway, times(1)).findAll(eq(aQuery));
    }


}
