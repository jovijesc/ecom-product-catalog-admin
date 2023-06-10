package com.ecom.catalog.admin.e2e;

import com.ecom.catalog.admin.domain.Identifier;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.product.ProductID;
import com.ecom.catalog.admin.infrastructure.category.models.CategoryResponse;
import com.ecom.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import com.ecom.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import com.ecom.catalog.admin.infrastructure.configuration.json.Json;
import com.ecom.catalog.admin.infrastructure.product.models.CreateProductRequest;
import com.ecom.catalog.admin.infrastructure.product.models.ProductResponse;
import com.ecom.catalog.admin.infrastructure.product.models.UpdateProductRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.money.MonetaryAmount;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();


    /**
     * Category
     */

    default CategoryID givenACategory(final String aName, final String aDescription, final boolean isActive) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(aName, aDescription, isActive);
        final var actualId = this.given("/categories", aRequestBody);
        return CategoryID.from(actualId);
    }

    default CategoryResponse retrieveACategory(final CategoryID anId) throws Exception {
        return this.retrieve("/categories/", anId, CategoryResponse.class);
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions updateACategory(final CategoryID anId, final UpdateCategoryRequest aRequest) throws Exception {
        return this.update("/categories/", anId, aRequest);
    }

    default ResultActions listCategories(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String directions) throws Exception {

        return this.list("/categories", page, perPage, search, sort, directions);
    }

    /**
     * Product
     */
    default ProductID givenAProduct(
            final String aName,
            final String aDescription,
            final String aStatus,
            final MonetaryAmount aPrice,
            final int aStock,
            final String aCategory,
            final String aStore,
            final int aImageMarkedAsFeatured
    ) throws Exception {
        final var aRequestBody = new CreateProductRequest(aName, aDescription, aStatus, aPrice, aStock, aCategory, aStore, aImageMarkedAsFeatured);
        final var actualId = this.given("/products", aRequestBody);
        return ProductID.from(actualId);
    }

    default ProductResponse retrieveAProduct(final ProductID anId) throws Exception {
        return this.retrieve("/products/", anId, ProductResponse.class);
    }

    default ResultActions listProducts(final int page, final int perPage) throws Exception {
        return listProducts(page, perPage, "", "", "");
    }

    default ResultActions listProducts(final int page, final int perPage, final String search) throws Exception {
        return listProducts(page, perPage, search, "", "");
    }

    default ResultActions updateAProduct(final ProductID anId, final UpdateProductRequest aRequest) throws Exception {
        return this.update("/products/", anId, aRequest);
    }

    default ResultActions listProducts(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String directions) throws Exception {

        return this.list("/products", page, perPage, search, sort, directions);
    }

    private String given(final String url, final Object body) throws Exception {
        final var aRequest = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");
        return actualId;
    }

    private <T> T retrieve(final String url, final Identifier anId, final Class<T> clazz) throws Exception {
        final var aRequest = get(url + anId.getValue())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return Json.readValue(json, clazz);
    }

    private ResultActions list(
            final String url,
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String directions) throws Exception {

        final var aRequest = get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", directions)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private ResultActions update(final String url, final Identifier anId, final Object aRequestBody) throws Exception {
        final var aRequest = put(url + anId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));
        return this.mvc().perform(aRequest);
    }
}
