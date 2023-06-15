package com.ecom.catalog.admin.e2e.product;

import com.ecom.catalog.admin.E2ETest;
import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.application.product.image.upload.UploadProductImagesCommand;
import com.ecom.catalog.admin.application.product.image.upload.UploadProductImagesOutput;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.domain.product.StoreGateway;
import com.ecom.catalog.admin.domain.utils.CollectionUtils;
import com.ecom.catalog.admin.e2e.MockDsl;
import com.ecom.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@E2ETest
@Testcontainers
public class ProductE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreGateway storeGateway;

    @Autowired
    private ObjectMapper mapper;


    @Container
    private static final MySQLContainer MYSQL_CONTAINER =
            new MySQLContainer("mysql:latest")
                    .withPassword("123456")
                    .withUsername("root")
                    .withDatabaseName("adm_products");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Override
    public ObjectMapper mapper() {
        return this.mapper;
    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToCreateANewProductWithValidValues() throws Exception {

        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var expectedCategory =
                givenACategory("Eletrônico", "Eletrônicos do tipo A", true);

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory;
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;

        final Set<MockMultipartFile> images = mockImages();

        final var expectedId = givenAProduct(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured, images);

        final var actualProduct = retrieveAProduct(expectedId);

        Assertions.assertEquals(expectedName, actualProduct.name());
        Assertions.assertEquals(expectedDescription, actualProduct.description());
        Assertions.assertEquals(expectedPrice, actualProduct.price());
        Assertions.assertEquals(expectedStock, actualProduct.stock());
        Assertions.assertEquals(expectedStatus.name(), actualProduct.status());
        Assertions.assertEquals(expectedCategory.getValue(), actualProduct.category());
        Assertions.assertNotNull(actualProduct.createdAt());
        Assertions.assertNotNull(actualProduct.updatedAt());

    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToNavigateToAllProducts() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var eletronicos = givenACategory("Eletronicos", "Eletronicos do tipo abc", true);
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;

        givenAProduct("Celular", "Celular do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(1800.03, "BRL"), 10, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);
        givenAProduct("Aspirador", "Aspirador do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(250.30, "BRL"), 15, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);

        listProducts(0,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Aspirador")));

        listProducts(1,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Celular")));

        listProducts(2,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToSearchBetweenAllProducts() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var eletronicos = givenACategory("Eletronicos", "Eletronicos do tipo abc", true);
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;


        givenAProduct("Celular", "Celular do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(1800.03, "BRL"), 10, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);
        givenAProduct("Aspirador", "Aspirador do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(250.30, "BRL"), 15, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);

        listProducts(0,1, "asp")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Aspirador")));
    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToSortAllProductsByDescriptionDesc() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var eletronicos = givenACategory("Eletronicos", "Eletronicos do tipo abc", true);
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;


        givenAProduct("Celular", "Celular do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(1800.03, "BRL"), 10, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);
        givenAProduct("Aspirador", "Aspirador do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(250.30, "BRL"), 15, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);
        givenAProduct("Batedeira", "Batedeira do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(80, "BRL"), 15, eletronicos.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);

        listProducts(0,3, "", "description", "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(3)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Celular")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Batedeira")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Aspirador")));
    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToGetAProductsByItsIdentifier() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var expectedCategory =
                givenACategory("Eletrônico", "Eletrônicos do tipo A", true);

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory;
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;

        final var expectedId = givenAProduct(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);

        final var actualProduct = retrieveAProduct(expectedId);

        Assertions.assertEquals(expectedName, actualProduct.name());
        Assertions.assertEquals(expectedDescription, actualProduct.description());
        Assertions.assertEquals(expectedPrice, actualProduct.price());
        Assertions.assertEquals(expectedStock, actualProduct.stock());
        Assertions.assertEquals(expectedStatus.name(), actualProduct.status());
        Assertions.assertEquals(expectedCategory.getValue(), actualProduct.category());
        Assertions.assertNotNull(actualProduct.createdAt());
        Assertions.assertNotNull(actualProduct.updatedAt());

    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundProducts() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var aRequest = get("/products/123")
                .contentType(MediaType.APPLICATION_JSON);

        final var actualId = this.mvc.perform(aRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("Product with ID 123 was not found")));
    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToUpdateAProductByItsIdentifier() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var expectedCategory =
                givenACategory("Eletrônico", "Eletrônicos do tipo A", true);

        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = expectedCategory;

        final var actualId = givenAProduct("Novo Celular", "Novo Celular do tipo ABC", ProductStatus.ACTIVE.name(), Money.of(1700.03, "BRL"), 20, expectedCategoryId.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);
        final var aRequestBody = new UpdateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStore.getId());

        updateAProduct(actualId, aRequestBody)
                .andExpect(status().isOk());

        final var actualProduct = productRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus.name(), actualProduct.getStatus().name());
        Assertions.assertEquals(expectedCategory.getValue(), actualProduct.getCategory().getId());
        Assertions.assertEquals(expectedStore.getId(), actualProduct.getStore().getId());
        Assertions.assertNotNull(actualProduct.getCreatedAt());
        Assertions.assertNotNull(actualProduct.getUpdatedAt());
    }

    @Test
    public void asACatalogProductAdminIShouldBeAbleToInactiveAProductByItsIdentifier() throws Exception {
        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());

        final var expectedCategory =
                givenACategory("Eletrônico", "Eletrônicos do tipo A", true);

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = expectedCategory;
        final var expectedStore =
                storeGateway.create(Fixture.Stores.lojaEletromania());
        final var expectedImageMarkedAsFeatured = 1;

        final var actualId = givenAProduct(expectedName, expectedDescription, ProductStatus.ACTIVE.name(), expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStore.getId(), expectedImageMarkedAsFeatured);

        final var aRequestBody =
                new UpdateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId.getValue(), expectedStore.getId());

        updateAProduct(actualId, aRequestBody)
                .andExpect(status().isOk());

        final var actualProduct = productRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus.name(), actualProduct.getStatus().name());
        Assertions.assertEquals(expectedCategory.getValue(), actualProduct.getCategory().getId());
        Assertions.assertEquals(expectedStore.getId(), actualProduct.getStore().getId());
        Assertions.assertNotNull(actualProduct.getCreatedAt());
        Assertions.assertNotNull(actualProduct.getUpdatedAt());
    }

    private static Set<MockMultipartFile> mockImages() {
        final var images = Set.of(
                new MockMultipartFile("images", "image01.jpg", MediaType.IMAGE_JPEG_VALUE, "IMAGE01".getBytes()),
                new MockMultipartFile("images", "image02.jpg", MediaType.IMAGE_JPEG_VALUE, "IMAGE02".getBytes())
        );
        return images;
    }

}
