package com.ecom.catalog.admin.domain.validation.handler;

import com.ecom.catalog.admin.domain.exceptions.DomainException;
import com.ecom.catalog.admin.domain.validation.Error;
import com.ecom.catalog.admin.domain.validation.ValidationHandler;

import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {

    @Override
    public ValidationHandler append(final Error anError) {
        throw DomainException.with(anError);
    }

    @Override
    public ValidationHandler append(ValidationHandler anHandler) {
        throw DomainException.with(anHandler.getErrors());
    }

    @Override
    public <T> T validate(Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final Exception e) {
            throw DomainException.with(new Error(e.getMessage()));
        }
    }

    @Override
    public List<Error> getErrors() {
        return List.of();
    }
}
