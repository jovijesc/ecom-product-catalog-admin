package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;
import com.ecom.catalog.admin.domain.validation.Validator;

import java.math.BigDecimal;

public class ProductValidator extends Validator {

    private static final int NAME_MAX_LENGTH = 255;
    private static final int NAME_MIN_LENGTH = 3;
    private static final int DESCRIPTIONS_MAX_LENGTH = 4000;
    private static final int STOCK_MIN_SIZE = 0;

    private static final BigDecimal PRICE_MIN_VALUE = BigDecimal.valueOf(0.01);

    private final Product product;


    public ProductValidator(final Product aProduct, final ValidationHandler aHandler) {
        super(aHandler);
        this.product = aProduct;
    }


    @Override
    public void validate() {
        checkNameConstraints();
        checkDescriptionConstraints();
        checkStockConstraints();
        checkPriceConstraints();
        checkCategoryConstraints();
        checkStoreConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.product.getName();
        if( name == null) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }

        if( name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be empty"));
            return;
        }

        final int length = name.trim().length();
        if(length > NAME_MAX_LENGTH || length < NAME_MIN_LENGTH) {
            this.validationHandler().append(new Error("'name' must be between 3 and 255 characters"));
        }
    }

    private void checkDescriptionConstraints() {
        final var description = this.product.getDescription();
        if( description == null) {
            this.validationHandler().append(new Error("'description' should not be null"));
            return;
        }

        if( description.isBlank()) {
            this.validationHandler().append(new Error("'description' should not be empty"));
            return;
        }

        final int length = description.trim().length();
        if(length > DESCRIPTIONS_MAX_LENGTH ) {
            this.validationHandler().append(new Error("'description' must be between 1 and 4000 characters"));
        }
    }

    private void checkStockConstraints() {
        final var stock = this.product.getStock();
        if(stock < STOCK_MIN_SIZE) {
            this.validationHandler().append(new Error("'stock' cannot have invalid values"));
        }
    }

    private void checkPriceConstraints() {
        final var price = this.product.getPrice();
        if(price == null ) {
            this.validationHandler().append(new Error("'price' should not be null"));
            return;
        }

        if(price.getAmount().compareTo(PRICE_MIN_VALUE) < 0) {
            this.validationHandler().append(new Error("'price' must be greater than zero"));
        }
    }

    private void checkCategoryConstraints() {
        final var categoryId = this.product.getCategoryId();
        if(categoryId == null) {
            this.validationHandler().append(new Error("'category' should not be null"));
        }
    }

    private void checkStoreConstraints() {
        final var store = this.product.getStore();
        if( store == null) {
            this.validationHandler().append(new Error("'store' should not be null"));
            return;
        }

        if( store.getId() == null) {
            this.validationHandler().append(new Error("'store.id' should not be null"));
        }
    }

}
