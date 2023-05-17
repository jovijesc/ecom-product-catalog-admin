package com.ecom.catalog.admin.application.category.retrieve.list;

import com.ecom.catalog.admin.application.category.retrieve.get.CategoryOutput;
import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryID;

import java.time.Instant;

public record CategoryListOutput(CategoryID id,
                                 String name,
                                 String description,
                                 boolean isActive,
                                 Instant createdAt
) {

    public static CategoryListOutput from(final Category aCategory) {
        return new CategoryListOutput(
                aCategory.getId(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.isActive(),
                aCategory.getCreatedAt()
        );
    }
}