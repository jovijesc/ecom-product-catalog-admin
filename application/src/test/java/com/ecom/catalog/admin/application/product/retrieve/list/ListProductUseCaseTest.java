package com.ecom.catalog.admin.application.product.retrieve.list;

import com.ecom.catalog.admin.application.product.UseCaseTest;
import com.ecom.catalog.admin.application.product.retrieve.list.DefaultListProductUseCase;
import com.ecom.catalog.admin.application.product.retrieve.list.ProductListOutput;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


class ListProductUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListProductUseCase useCase;

    @Mock
    private ProductGateway productGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(productGateway);
    }

    @Test
    public void givenAValidQuery_whenCallsListProduct_shouldReturnProducts() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image",1, true));
        final var products = List.of(
                Product.newProduct("Celular", "Celular do tipo ABC", ProductStatus.ACTIVE, Money.with(3000.0), 10, CategoryID.from("123"), expectedStore, expectedImages),
                Product.newProduct("Notebook", "Notebook do tipo 123", ProductStatus.ACTIVE, Money.with(5000.0), 10, CategoryID.from("456"), expectedStore, expectedImages)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedItems = products.stream()
                .map(ProductListOutput::from)
                .toList();

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedTotal,
            products
        );

        when(productGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        Mockito.verify(productGateway, times(1)).findAll(eq(aQuery));

    }

    @Test
    public void givenAValidQuery_whenCallsListProductAndResultIsEmpty_shouldReturnProducts() {
        // given
        final var products = List.<Product>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = List.<ProductListOutput>of();

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                products
        );

        when(productGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        Mockito.verify(productGateway, times(1)).findAll(eq(aQuery));

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

        when(productGateway.findAll(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

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