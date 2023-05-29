package com.ecom.catalog.admin.infrastructure.api;

import com.ecom.catalog.admin.ControllerTest;
import com.ecom.catalog.admin.application.product.create.CreateProductOutput;
import com.ecom.catalog.admin.application.product.create.CreateProductUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.GetProductByIdUseCase;
import com.ecom.catalog.admin.application.product.retrieve.get.ProductOutput;
import com.ecom.catalog.admin.application.product.update.UpdateProductOutput;
import com.ecom.catalog.admin.application.product.update.UpdateProductUseCase;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.product.Product;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.handler.Notification;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import com.ecom.catalog.admin.infrastructure.utils.MoneyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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

    @Test
    public void givenAValidCommand_whenCallsCreateProduct_shouldReturnProductId() throws Exception {
        // given
        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";
        final var expectedId = "123";

        final var anInput =
                new CreateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId);

        when(createProductUseCase.execute(any()))
                .thenReturn(CreateProductOutput.from(expectedId));

        // when
        final var aRequest = post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/products/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

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
    public void givenAnInvalidNullName_whenCallsCreateProduct_shouldReturnDomainException() throws Exception {
        // given
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = "123";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput =
                new CreateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId);

        when(createProductUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));
        // when
        final var aRequest = post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

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

        final var aProduct =
                Product.newProduct("Celular A", "Celular do tipo BBB", com.ecom.catalog.admin.domain.product.Money.with(1700.0), 10, CategoryID.from(expectedCategoryId));

        final var expectedId = aProduct.getId().getValue();

        final var anInput =
                new UpdateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId);

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
        ));
    }

    @Test
    public void givenAnInvalidNullName_whenCallsUpdateProduct_shouldReturnNotificationException() throws Exception {
        // given
        final var expectedCategoryId = "123";
        final var aProduct =
                Product.newProduct("Celular A", "Celular do tipo BBB", com.ecom.catalog.admin.domain.product.Money.with(1700.0), 10, CategoryID.from(expectedCategoryId));

        final var expectedId = aProduct.getId();
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.of(1800.03, "BRL");
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput =
                new UpdateProductRequest(expectedName, expectedDescription, expectedStatus.name(), expectedPrice, expectedStock, expectedCategoryId);

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
        final var aProduct =
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, CategoryID.from(expectedCategoryId));

        final var expectedId = aProduct.getId();

        when(getProductByIdUseCase.execute(any()))
                .thenReturn(ProductOutput.from(aProduct));

        // when
        final var aRequest = get("/products/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.status", equalTo(expectedStatus.name())))
                .andExpect(jsonPath("$.stock", equalTo(expectedStock)))
                .andExpect(jsonPath("$.price.amount", equalTo(expectedPrice.getAmount().toString())))
                .andExpect(jsonPath("$.price.currency", equalTo(expectedPrice.getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.created_at", equalTo(aProduct.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aProduct.getUpdatedAt().toString())));

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

}
