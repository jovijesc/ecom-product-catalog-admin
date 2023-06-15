package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;
import com.ecom.catalog.admin.domain.validation.Validator;

import java.math.BigDecimal;

public class ProductImageValidator extends Validator {

    private final ProductImage image;


    public ProductImageValidator(final ProductImage aImage, final ValidationHandler aHandler) {
        super(aHandler);
        this.image = aImage;
    }


    @Override
    public void validate() {
        checkChecksumConstraints();
        checkNameConstraints();
        checkLocationConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.image.getName();
        if( name == null) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }

        if( name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be empty"));
        }
    }

    private void checkLocationConstraints() {
        final var location = this.image.getLocation();
        if( location == null) {
            this.validationHandler().append(new Error("'location' should not be null"));
            return;
        }

        if( location.isBlank()) {
            this.validationHandler().append(new Error("'location' should not be empty"));
        }
    }

    private void checkChecksumConstraints() {
        final var checksum = this.image.getChecksum();
        if( checksum == null) {
            this.validationHandler().append(new Error("'checksum' should not be null"));
            return;
        }

        if( checksum.isBlank()) {
            this.validationHandler().append(new Error("'checksum' should not be empty"));
        }
    }


}
