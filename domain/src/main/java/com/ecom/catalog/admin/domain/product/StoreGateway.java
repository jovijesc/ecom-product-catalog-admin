package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.category.CategoryID;

public interface StoreGateway {

    Store create(Store aStore);
    boolean existsById(String id);
}
