package com.ecom.catalog.admin.application.create;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.ProductGateway;
import com.ecom.catalog.admin.domain.product.ProductStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @InjectMocks
    private DefaultCreateProductUseCase useCase;

    @Mock
    private ProductGateway productGateway;

    @Test
    public void givenAValidCommand_whenCallsCreateProduct_shouldReturnProductId() {
        // given
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;

        final var aCommand =
                CreateProductCommand.with(expectedName, expectedDescription, expectedPrice, expectedStock);

        when(productGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(productGateway).create(Mockito.argThat(aProduct ->
            Objects.equals(expectedName, aProduct.getName())
                    && Objects.equals(expectedDescription, aProduct.getDescription())
                    && Objects.equals(expectedPrice, aProduct.getPrice())
                    && Objects.equals(expectedStock, aProduct.getStock())
                    && Objects.equals(expectedStatus, aProduct.getStatus())
                    && Objects.nonNull(aProduct.getId())
                    && Objects.nonNull(aProduct.getCreatedAt())
                    && Objects.nonNull(aProduct.getUpdatedAt())
        ));

    }

}