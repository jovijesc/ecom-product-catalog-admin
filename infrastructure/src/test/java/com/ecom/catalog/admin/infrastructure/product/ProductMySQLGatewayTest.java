package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.MySQLGatewayTest;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.infrastructure.category.CategoryMySQLGateway;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductJpaEntity;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@MySQLGatewayTest
public class ProductMySQLGatewayTest {

    @Autowired
    private DefaultProductGateway productGateway;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidProduct_whenCallsCreateProduct_shouldPersistProduct() {
        // given

        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());


        final var aProduct = Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategory.getId(), expectedStore, expectedImages);

        final var expectedId = aProduct.getId();

        Assertions.assertEquals(0, productRepository.count());

        // when
        final var actualProduct = productGateway.create(aProduct);

        // then
        Assertions.assertEquals(1, productRepository.count());

        Assertions.assertNotNull(actualProduct);;
        Assertions.assertEquals(expectedId, actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(aProduct.getStatus(), actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(expectedImages, actualProduct.getImages());

        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), actualProduct.getUpdatedAt());

        final var persistedProduct = productRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedProduct.getName());
        Assertions.assertEquals(expectedDescription, persistedProduct.getDescription());
        Assertions.assertEquals(expectedPrice, MoneyUtils.fromMonetaryAmount(persistedProduct.getPrice()));
        Assertions.assertEquals(expectedStock, persistedProduct.getStock());
        Assertions.assertEquals(aProduct.getStatus(), persistedProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), persistedProduct.getCategory().toAggregate().getId());
        Assertions.assertEquals(expectedStore.getId(), persistedProduct.getStore().getId());
        Assertions.assertEquals(aProduct.getCreatedAt(), persistedProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), persistedProduct.getUpdatedAt());
    }


    @Test
    public void givenAValidProductInactive_whenCallsUpdateProductActivating_shouldPersistProduct() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());


        final var aProduct = Product.newProduct(expectedName, expectedDescription, ProductStatus.INACTIVE, expectedPrice, expectedStock, expectedCategory.getId(), expectedStore, expectedImages);

        final var expectedId = aProduct.getId();

        Assertions.assertEquals(0, productRepository.count());

        productRepository.saveAndFlush(ProductJpaEntity.from(aProduct));

        Assertions.assertEquals(aProduct.getStatus(), ProductStatus.INACTIVE);

        // when
        final var actualProduct = productGateway.update(
                Product.with(aProduct)
                        .update(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategory.getId(), expectedStore, expectedImages)
        );

        // then
        Assertions.assertEquals(1, productRepository.count());

        Assertions.assertNotNull(actualProduct);;
        Assertions.assertEquals(expectedId, actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(expectedImages, actualProduct.getImages());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertTrue(aProduct.getUpdatedAt().isBefore(actualProduct.getUpdatedAt()));

        final var persistedProduct = productRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedProduct.getName());
        Assertions.assertEquals(expectedDescription, persistedProduct.getDescription());
        Assertions.assertEquals(expectedPrice, MoneyUtils.fromMonetaryAmount(persistedProduct.getPrice()));
        Assertions.assertEquals(expectedStock, persistedProduct.getStock());
        Assertions.assertEquals(expectedStatus, persistedProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), persistedProduct.getCategory().toAggregate().getId());
        Assertions.assertEquals(expectedStore.getId(), persistedProduct.getStore().getId());
        Assertions.assertEquals(aProduct.getCreatedAt(), persistedProduct.getCreatedAt());
        Assertions.assertTrue(aProduct.getUpdatedAt().isBefore(persistedProduct.getUpdatedAt()));

    }

    @Test
    public void givenAValidProductActive_whenCallsUpdateProductInactivating_shouldPersistProduct() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var aProduct = Product.newProduct(expectedName, expectedDescription, ProductStatus.ACTIVE, expectedPrice, expectedStock, expectedCategory.getId(), expectedStore, expectedImages);

        final var expectedId = aProduct.getId();

        Assertions.assertEquals(0, productRepository.count());

        productRepository.saveAndFlush(ProductJpaEntity.from(aProduct));

        Assertions.assertEquals(aProduct.getStatus(), ProductStatus.ACTIVE);

        // when
        final var actualProduct = productGateway.update(
                Product.with(aProduct)
                        .update(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategory.getId(), expectedStore, expectedImages)
        );

        // then
        Assertions.assertEquals(1, productRepository.count());

        Assertions.assertNotNull(actualProduct);;
        Assertions.assertEquals(expectedId, actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(expectedImages, actualProduct.getImages());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertTrue(aProduct.getUpdatedAt().isBefore(actualProduct.getUpdatedAt()));

        final var persistedProduct = productRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedProduct.getName());
        Assertions.assertEquals(expectedDescription, persistedProduct.getDescription());
        Assertions.assertEquals(expectedPrice, MoneyUtils.fromMonetaryAmount(persistedProduct.getPrice()));
        Assertions.assertEquals(expectedStock, persistedProduct.getStock());
        Assertions.assertEquals(expectedStatus, persistedProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), persistedProduct.getCategory().toAggregate().getId());
        Assertions.assertEquals(expectedStore.getId(), persistedProduct.getStore().getId());
        Assertions.assertEquals(aProduct.getCreatedAt(), persistedProduct.getCreatedAt());
        Assertions.assertTrue(aProduct.getUpdatedAt().isBefore(persistedProduct.getUpdatedAt()));

    }

    @Test
    public void givenAPrePersistedProduct_whenCallsFindById_shouldReturnProduct() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var aProduct = Product.newProduct(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategory.getId(), expectedStore, expectedImages);

        final var expectedId = aProduct.getId();

        productRepository.saveAndFlush(ProductJpaEntity.from(aProduct));

        Assertions.assertEquals(1, productRepository.count());

        // when
        final var actualProduct = productGateway.findById(expectedId).get();

        Assertions.assertEquals(expectedId, actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategory.getId(), actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore.getId(), actualProduct.getStore().getId());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), actualProduct.getUpdatedAt());

    }

    @Test
    public void givenAnInvalidProductId_whenCallsFindById_shouldReturnEmpty() {
        // given
        final var expectedId = ProductID.from("123");

        Assertions.assertEquals(0, productRepository.count());

        // when
        final var actualProduct = productGateway.findById(expectedId);

        // then
        Assertions.assertTrue(actualProduct.isEmpty());
    }

    @Test
    public void givenEmptyProducts_whenCallFindAll_shouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms,expectedSort, expectedDirection);

        // when
        final var actualPage = productGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "celu,0,10,1,1,Celular",
            "tabl,0,10,1,1,Tablet",
            "carr,0,10,1,1,Carregador",
            "moch,0,10,1,1,Mochila",
            "pen,0,10,1,1,Penal",
    })

    public void givenAValidTerm_whenCallFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ) {
        // given
        mockProducts();
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms,expectedSort, expectedDirection);

        // when
        final var actualPage = productGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedGenreName, actualPage.items().get(0).getName());

    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Carregador",
            "name,desc,0,10,5,5,Tablet",
            "createdAt,asc,0,10,5,5,Celular",
            "createdAt,desc,0,10,5,5,Penal",
    })
    public void givenAValidSortAndDirection_whenCallFindAll_shouldReturnFiltered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ) {
        // given
        mockProducts();
        final var expectedTerms = "";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms,expectedSort, expectedDirection);

        // when
        final var actualPage = productGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedGenreName, actualPage.items().get(0).getName());

    }


    private void mockProducts() {

        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var expectedCategoryOne =  categoryRepository.saveAndFlush(CategoryJpaEntity.from(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true)));
        final var expectedCategoryTwo =  categoryRepository.saveAndFlush(CategoryJpaEntity.from(Category.newCategory("Papelaria", "Papelaria do tipo A", true)));

        productRepository.saveAllAndFlush(List.of(
                ProductJpaEntity.from(Product.newProduct("Celular", "Celular com outra descrição", expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryOne.getId()), expectedStore, expectedImages)),
                ProductJpaEntity.from(Product.newProduct("Tablet", "Tablet com outra descrição", expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryOne.getId()), expectedStore, expectedImages)),
                ProductJpaEntity.from(Product.newProduct("Carregador", "Carregador com outra descrição", expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryOne.getId()), expectedStore, expectedImages)),
                ProductJpaEntity.from(Product.newProduct("Mochila", "Mochila com outra descrição", expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryTwo.getId()), expectedStore, expectedImages)),
                ProductJpaEntity.from(Product.newProduct("Penal", "Penal com outra descrição", expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryTwo.getId()), expectedStore, expectedImages))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Carregador;Celular",
            "1,2,2,5,Mochila;Penal",
            "2,2,1,5,Tablet",
    })
    public void givenAValidPaging_whenCallFindAll_shouldReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenres
    ) {
        // given
        mockProducts();
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms,expectedSort, expectedDirection);

        // when
        final var actualPage = productGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for(final var expectedName : expectedGenres.split(";")) {
            final var actualName = actualPage.items().get(index).getName();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }

    }

    @Test
    public void givenTwoProductsAndOnePersisted_whenCallsExistsByIds_shouldReturnPersistedID() {
        // given
        final var expectedCategory =
                categoryGateway.create(Category.newCategory("Eletrônico", "Eletrônicos do tipo A", true));
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var aProduct =
                Product.newProduct("Celular 1", "Celular descricao", ProductStatus.ACTIVE, Money.with(1800.0),10, expectedCategory.getId(), expectedStore, expectedImages);

        final var expectedItems = 1;
        final var expectedId = aProduct.getId();

        Assertions.assertEquals(0, productRepository.count());

        productRepository.saveAndFlush(ProductJpaEntity.from(aProduct));

        // when
        final var actualGenre = productGateway.existsByIds(List.of(ProductID.from("123"), expectedId));

        // then
        Assertions.assertEquals(expectedItems, actualGenre.size());
        Assertions.assertEquals(expectedId.getValue(), actualGenre.get(0).getValue());

    }

}
