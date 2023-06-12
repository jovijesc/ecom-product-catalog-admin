package com.ecom.catalog.admin.infrastructure.api;

import com.ecom.catalog.admin.ControllerTest;
import com.ecom.catalog.admin.application.category.retrieve.get.CategoryOutput;
import com.ecom.catalog.admin.application.product.create.CreateProductCommand;
import com.ecom.catalog.admin.application.product.create.CreateProductOutput;
import com.ecom.catalog.admin.application.product.create.CreateProductUseCase;
import com.ecom.catalog.admin.application.product.image.get.GetProductImageCommand;
import com.ecom.catalog.admin.application.product.image.get.GetProductImageUseCase;
import com.ecom.catalog.admin.application.product.image.get.ProductImageOutput;
import com.ecom.catalog.admin.application.product.retrieve.get.GetProductByIdUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.ProductOutput;
import com.ecom.catalog.admin.application.product.retrieve.list.ListProductUseCase;
import com.ecom.catalog.admin.application.product.retrieve.list.ProductListOutput;
import com.ecom.catalog.admin.application.product.update.UpdateProductOutput;
import com.ecom.catalog.admin.application.product.update.UpdateProductUseCase;
import com.ecom.catalog.admin.domain.Fixture;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.handler.Notification;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.ProductImageResponse;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import com.ecom.catalog.admin.infrastructure.utils.ImageTypeUtils;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = ProductAPI.class)
public class ProductAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private UpdateProductUseCase updateProductUseCase;

    @MockBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockBean
    private ListProductUseCase listProductUseCase;

    @MockBean
    private GetProductImageUseCase getProductImageUseCase;


    @Test
    public void givenAValidCommandWithImages_whenCallsCreateProduct_shouldReturnProductId() throws Exception {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";
        final var expectedId = "123";
        final var expectedStoreId = "123";
        final var expectedImageMarkedAsFeatured = 1;

        final var anInput =
                new CreateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId, expectedStoreId, expectedImageMarkedAsFeatured);

        final var expectedProduct = new MockMultipartFile("product", null,
                MediaType.APPLICATION_JSON_VALUE, this.mapper.writeValueAsBytes(anInput));

        final var expectedImage1 = new MockMultipartFile("images", "image01.jpg", MediaType.IMAGE_JPEG_VALUE, "IMAGE01".getBytes());
        final var expectedImage2 = new MockMultipartFile("images", "image02.jpg", MediaType.IMAGE_JPEG_VALUE, "IMAGE02".getBytes());

        when(createProductUseCase.execute(any()))
                .thenReturn(CreateProductOutput.from(expectedId));


        // Create multipart request
        final var aRequest = multipart("/products")
                .file(expectedProduct)
                .file(expectedImage1)
                .file(expectedImage2)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/products/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        verify(createProductUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedName, actualCmd.name());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedStatus, actualCmd.status());
        Assertions.assertEquals(expectedStock, actualCmd.stock());
        Assertions.assertEquals(MoneyUtils.fromMonetaryAmount(expectedPrice), actualCmd.price());
        Assertions.assertEquals(expectedCategoryId, actualCmd.category());
        Assertions.assertEquals(expectedStoreId, actualCmd.category());
        Assertions.assertEquals(
                Stream.of(expectedImage1, expectedImage2)
                        .map(MockMultipartFile::getOriginalFilename)
                        .collect(Collectors.toSet()),
                actualCmd.images().stream()
                        .map(ProductImage::getName)
                        .collect(Collectors.toSet())
        );

    }

    @Test
    public void givenAValidCommandWithoutImages_whenCallsCreateProduct_shouldReturnProductId() throws Exception {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";
        final var expectedId = "123";
        final var expectedStoreId = "123";
        final var expectedImageMarkedAsFeatured = 1;

        final var anInput =
                new CreateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId, expectedStoreId, expectedImageMarkedAsFeatured);

        final var expectedProduct = new MockMultipartFile("product", null,
                MediaType.APPLICATION_JSON_VALUE, this.mapper.writeValueAsBytes(anInput));

        when(createProductUseCase.execute(any()))
                .thenReturn(CreateProductOutput.from(expectedId));


        // Create multipart request
        final var aRequest = multipart("/products")
                .file(expectedProduct)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/products/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        verify(createProductUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedName, actualCmd.name());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedStatus, actualCmd.status());
        Assertions.assertEquals(expectedStock, actualCmd.stock());
        Assertions.assertEquals(MoneyUtils.fromMonetaryAmount(expectedPrice), actualCmd.price());
        Assertions.assertEquals(expectedCategoryId, actualCmd.category());
        Assertions.assertEquals(expectedStoreId, actualCmd.category());
        Assertions.assertTrue(actualCmd.images() == null || actualCmd.images().isEmpty());

    }

    @Test
    public void givenAnInvalidNullName_whenCallsCreateProduct_shouldReturnDomainException() throws Exception {
        // given
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";
        final var expectedStoreId = "123";
        final var expectedImageMarkedAsFeatured = 1;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput =
                new CreateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId, expectedStoreId, expectedImageMarkedAsFeatured);

        final var expectedProduct = new MockMultipartFile("product", null,
                MediaType.APPLICATION_JSON_VALUE, this.mapper.writeValueAsBytes(anInput));

        when(createProductUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));
        // when
        final var aRequest = multipart("/products")
                .file(expectedProduct)

                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createProductUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedStatus, cmd.status())
                        && Objects.equals(expectedStock, cmd.stock())
                        && Objects.equals(MoneyUtils.fromMonetaryAmount(expectedPrice), cmd.price())
                        && Objects.equals(expectedCategoryId, cmd.category())
        ));

    }

    @Test
    public void givenAValidCommand_whenCallsUpdateProduct_shouldReturnProductId() throws Exception {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";
        final var expectedStore = Fixture.Stores.lojaEletromania();

        final var aProduct =
                Product.newProduct("Celular A", "Celular do tipo BBB", expectedStatus, com.ecom.catalog.admin.domain.product.Money.with(1700.0), 10, CategoryID.from(expectedCategoryId), expectedStore);

        final var expectedId = aProduct.getId().getValue();

        final var anInput =
                new UpdateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId, expectedStore.getId());

        when(updateProductUseCase.execute(any()))
                .thenReturn(UpdateProductOutput.from(aProduct));

        // when
        final var aRequest = put("/products/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateProductUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedStatus, cmd.status())
                        && Objects.equals(expectedStock, cmd.stock())
                        && Objects.equals(MoneyUtils.fromMonetaryAmount(expectedPrice), cmd.price())
                        && Objects.equals(expectedCategoryId, cmd.category())
                        && Objects.equals(expectedStore.getId(), cmd.store())
        ));
    }

    @Test
    public void givenAnInvalidNullName_whenCallsUpdateProduct_shouldReturnNotificationException() throws Exception {
        // given
        final var expectedCategoryId = "123";

        final var aProduct =
                Product.newProduct("Celular A", "Celular do tipo BBB", ProductStatus.ACTIVE, com.ecom.catalog.admin.domain.product.Money.with(1700.0), 10, CategoryID.from(expectedCategoryId), Fixture.Stores.lojaEletromania());

        final var expectedId = aProduct.getId();
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStore = Fixture.Stores.lojaEletromania();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput =
                new UpdateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId, expectedStore.getId());

        when(updateProductUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        // when
        final var aRequest = put("/products/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateProductUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedStatus, cmd.status())
                        && Objects.equals(expectedStock, cmd.stock())
                        && Objects.equals(MoneyUtils.fromMonetaryAmount(expectedPrice), cmd.price())
                        && Objects.equals(expectedCategoryId, cmd.category())
                        && Objects.equals(expectedStore.getId(), cmd.store())
        ));

    }

    @Test
    public void givenAValidId_whenCallsGetProduct_shouldReturnProduct() throws Exception {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = com.ecom.catalog.admin.domain.product.Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var expectedImages = Set.of(Fixture.ProductImages.img01());

        final var aProduct =
                Product.newProduct(expectedName, expectedDescription,expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryId), expectedStore, expectedImages);

        final var expectedId = aProduct.getId();

        when(getProductByIdUseCase.execute(any()))
                .thenReturn(ProductOutput.from(aProduct));

        // when
        final var aRequest = get("/products/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.status", equalTo(expectedStatus.name())))
                .andExpect(jsonPath("$.stock", equalTo(expectedStock)))
                .andExpect(jsonPath("$.price.amount", equalTo(expectedPrice.getAmount().toString())))
                .andExpect(jsonPath("$.price.currency", equalTo(expectedPrice.getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.category", equalTo(expectedCategoryId)))
                .andExpect(jsonPath("$.store", equalTo(expectedStore.getId())))
                .andExpect(jsonPath("$.created_at", equalTo(aProduct.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aProduct.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images", hasSize(expectedImages.size())));

        for (int i = 0; i < expectedImages.size(); i++) {

            final var expectedImage = (ProductImage) expectedImages.toArray()[i];
            final var imageJsonPath = String.format("$.images[%d]", i);

            response.andExpect(jsonPath(imageJsonPath + ".id", equalTo(expectedImage.getId().getValue())))
                    .andExpect(jsonPath(imageJsonPath + ".checksum", equalTo(expectedImage.getChecksum())))
                    .andExpect(jsonPath(imageJsonPath + ".name", equalTo(expectedImage.getName())))
                    .andExpect(jsonPath(imageJsonPath + ".location", equalTo(expectedImage.getLocation())))
                    .andExpect(jsonPath(imageJsonPath + ".featured", equalTo(expectedImage.isFeatured())));
        }

        // then
        Mockito.verify(getProductByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenANonExistentId_whenCallGetProductAndDoesNotExists_shouldReturnNotFound() throws Exception {
        // given
        final var expectedErrorMessage = "Product with ID 123 was not found";

        final var expectedId = ProductID.from("123");

        when(getProductByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Product.class, expectedId));

        // when
        final var aRequest = get("/products/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getProductByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidQuery_whenCallsListProduct_shouldReturnProducts() throws Exception {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = com.ecom.catalog.admin.domain.product.Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = "123";
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedStore = Fixture.Stores.lojaEletromania();
        final var aProduct =
                Product.newProduct(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, CategoryID.from(expectedCategoryId), expectedStore);

        final var expectedItems = List.of(ProductListOutput.from(aProduct));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "e";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        when(listProductUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/products")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aProduct.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aProduct.getName())))
                .andExpect(jsonPath("$.items[0].status", equalTo(aProduct.getStatus().name())))
                .andExpect(jsonPath("$.items[0].stock", equalTo(aProduct.getStock())))
                .andExpect(jsonPath("$.items[0].price.amount", equalTo(aProduct.getPrice().getAmount().toString())))
                .andExpect(jsonPath("$.items[0].price.currency", equalTo(aProduct.getPrice().getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.items[0].category", equalTo(aProduct.getCategoryId().getValue())))
                .andExpect(jsonPath("$.items[0].store", equalTo(aProduct.getStore().getId())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aProduct.getCreatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(SearchQuery.class);

        verify(listProductUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
    }

    @Test
    public void givenAValidQuery_whenCallsListProductAndResultIsEmpty_shouldReturnProducts() throws Exception {
        // given
        final var expectedItems = List.<ProductListOutput>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "e";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        when(listProductUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/products")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)));

        final var captor = ArgumentCaptor.forClass(SearchQuery.class);

        verify(listProductUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());

    }

    @Test
    public void givenAValidProductIdAndImageId_whenCallsGetProductImage_shouldReturnContent() throws Exception {
        // given
        final var expectedProductId = ProductID.unique();
        final var expectedImage = Fixture.ProductImages.img01();
        final var expectedImageId = expectedImage.getId();
        final var expectedImageOutput = ProductImageOutput.with(expectedImage);

        when(getProductImageUseCase.execute(any())).thenReturn(expectedImageOutput);

        // when
        final var aRequest = get("/products/{id}/images/{idImage}",
                expectedProductId.getValue(), expectedImageId.getValue());

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, ImageTypeUtils.getContentType(expectedImage.getName())))
                .andExpect(header().string(CONTENT_LENGTH, String.valueOf(expectedImageOutput.content().length)))
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedImageOutput.name())))
                .andExpect(content().bytes(expectedImageOutput.content()));

        final var captor = ArgumentCaptor.forClass(GetProductImageCommand.class);

        verify(this.getProductImageUseCase).execute(captor.capture());

        final var actualCommand = captor.getValue();
        Assertions.assertEquals(expectedProductId.getValue(), actualCommand.productId());
        Assertions.assertEquals(expectedImageId.getValue(), actualCommand.imageId());

    }



}
