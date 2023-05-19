package com.ecom.catalog.admin;

import com.ecom.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public class MySQLCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var appContext = SpringExtension.getApplicationContext(context);
        cleanUp(List.of(
                appContext.getBean(ProductRepository.class),
                appContext.getBean(CategoryRepository.class)
        ));

    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}
