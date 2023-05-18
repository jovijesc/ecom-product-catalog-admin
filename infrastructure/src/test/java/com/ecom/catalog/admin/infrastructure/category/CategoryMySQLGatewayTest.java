package com.ecom.catalog.admin.infrastructure.category;

import com.ecom.catalog.admin.MySQLGatewayTest;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.pagination.SearchQuery;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_shouldPersistCategory() {
        // given
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        // when
        final var actualCategory = categoryGateway.create(aCategory);

        // then
        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    public void givenAValidCategoryInactive_whenCallsUpdateCategoryActivating_shouldPersistCategory() {
        // given
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, false);

        final var expectedId = aCategory.getId();

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertFalse(aCategory.isActive());
        Assertions.assertNotNull(aCategory.getDeletedAt());

        // when
        final var actualCategory = categoryGateway.update(
                Category.with(aCategory).update(expectedName, expectedDescription, expectedIsActive)
        );

        // then
        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(expectedId, actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var persistedCategory = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(expectedName, persistedCategory.getName());
        Assertions.assertEquals(expectedDescription, persistedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, persistedCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), persistedCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(persistedCategory.getUpdatedAt()));
        Assertions.assertNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCategoryActive_whenCallsUpdateCategoryInactivating_shouldPersistCategory() {
        // given
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = false;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, true);

        final var expectedId = aCategory.getId();

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertTrue(aCategory.isActive());
        Assertions.assertNull(aCategory.getDeletedAt());

        // when
        final var actualCategory = categoryGateway.update(
                Category.with(aCategory).update(expectedName, expectedDescription, expectedIsActive)
        );

        // then
        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(expectedId, actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNotNull(actualCategory.getDeletedAt());

        final var persistedCategory = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(expectedName, persistedCategory.getName());
        Assertions.assertEquals(expectedDescription, persistedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, persistedCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), persistedCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(persistedCategory.getUpdatedAt()));
        Assertions.assertNotNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void givenAPrePersistedCategory_whenCallsDeleteById_shouldDeleteCategory() {
        // given
        final var aCategory = Category.newCategory("Eletronicos", "Eletronicos do tipo a", true);

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        // when
        categoryGateway.deleteById(aCategory.getId());

        // then
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidCategory_whenCallsDeleteById_shouldReturnOk() {
        // given
        Assertions.assertEquals(0, categoryRepository.count());

        // when
        categoryGateway.deleteById(CategoryID.from("123"));

        // then
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategory_whenCallsFindById_shouldReturnCategory() {
        // given
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = aCategory.getId();

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        // when
        final var actualCategory = categoryGateway.findById(expectedId).get();

        // then
        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(expectedId, actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

    }

    @Test
    public void givenAnInvalidCategoryId_whenCallsFindById_shouldReturnEmpty() {
        // given
        final var expectedId = CategoryID.from("123");

        Assertions.assertEquals(0, categoryRepository.count());

        // when
        final var actualCategory = categoryGateway.findById(expectedId);

        // then
        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    public void givenEmptyCategories_whenCallFindAll_shouldReturnEmptyList() {
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
        final var actualPage = categoryGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }
}
