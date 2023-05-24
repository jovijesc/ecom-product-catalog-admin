package com.ecom.catalog.admin.infrastructure.api;

import com.ecom.catalog.admin.ControllerTest;
import com.ecom.catalog.admin.application.category.create.CreateCategoryCommand;
import com.ecom.catalog.admin.application.category.create.CreateCategoryOutput;
import com.ecom.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.ecom.catalog.admin.application.category.retrieve.get.CategoryOutput;
import com.ecom.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.ecom.catalog.admin.application.category.retrieve.list.CategoryListOutput;
import com.ecom.catalog.admin.application.category.retrieve.list.ListCategoryUseCase;
import com.ecom.catalog.admin.application.category.update.UpdateCategoryCommand;
import com.ecom.catalog.admin.application.category.update.UpdateCategoryOutput;
import com.ecom.catalog.admin.application.category.update.UpdateCategoryUseCase;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotFoundException;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.pagination.Pagination;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.handler.Notification;
import com.ecom.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import com.ecom.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private ListCategoryUseCase listCategoryUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
        // given
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;
        final var expectedId = "123";

        final var anInput =
                new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(CreateCategoryOutput.from(expectedId));

        // when
        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo("123")));

        Mockito.verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidNullName_whenCallsCreateCategory_shouldThrowsNotificationException() throws Exception {
        // given
        final String expectedName = null;
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        // when
        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        Mockito.verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() throws Exception {
        // given
        final var expectedId = "123";
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var anInput =
                new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(updateCategoryUseCase.execute(any()))
                .thenReturn(UpdateCategoryOutput.from(expectedId));

        // when
        final var request = put("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo("123")));

        Mockito.verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));

    }

    @Test
    public void givenAnInvalidNullName_whenCallsUpdateCategory_shouldThrowsNotificationException() throws Exception {
        // given
        final var expectedId = "123";
        final String expectedName = null;
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(updateCategoryUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        // when
        final var request = put("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        Mockito.verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() throws Exception {
        // given
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = aCategory.getId();

        when(getCategoryByIdUseCase.execute(any()))
                .thenReturn(CategoryOutput.from((aCategory)));

        // when
        final var aRequest = get("/categories/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.created_at", equalTo(aCategory.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aCategory.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(aCategory.getDeletedAt())));

        Mockito.verify(getCategoryByIdUseCase).execute(eq(expectedId.getValue()));

    }

    @Test
    public void givenANonExistentId_whenCallGetCategoryAndDoesNotExists_shouldReturnNotFound() throws Exception {
        // given
        final var expectedErrorMessage = "Category with ID 123 was not found";

        final var expectedId = CategoryID.from("123");

        when(getCategoryByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, expectedId));

        // when
        final var aRequest = get("/categories/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(getCategoryByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidQuery_whenCallsListCategories_shouldReturnAll() throws Exception {
        // given
        final var aCategoryOne = Category.newCategory("Automotivo", "Automotivo do tipo C",true);
        final var aCategoryTwo = Category.newCategory("Artesanato", "Artesanato do tipo A",true);
        final var categories = List.of(aCategoryOne, aCategoryTwo);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedTotal = 2;
        final var expectedItemsCount = 2;

        final var expectedItems = categories.stream()
                .map(CategoryListOutput::from)
                .toList();

        when(listCategoryUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/categories")
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
                .andExpect(jsonPath("$.items[0].id", equalTo(aCategoryOne.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aCategoryOne.getName())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(aCategoryOne.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aCategoryOne.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aCategoryOne.getDeletedAt())))
                .andExpect(jsonPath("$.items[1].id", equalTo(aCategoryTwo.getId().getValue())))
                .andExpect(jsonPath("$.items[1].name", equalTo(aCategoryTwo.getName())))
                .andExpect(jsonPath("$.items[1].is_active", equalTo(aCategoryTwo.isActive())))
                .andExpect(jsonPath("$.items[1].created_at", equalTo(aCategoryTwo.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[1].deleted_at", equalTo(aCategoryTwo.getDeletedAt())));


        verify(listCategoryUseCase).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));

    }

}
