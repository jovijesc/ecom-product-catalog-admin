package com.ecom.catalog.admin.infrastructure.category;

import com.ecom.catalog.admin.MySQLGatewayTest;
import com.ecom.catalog.admin.domain.category.Category;
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
}
