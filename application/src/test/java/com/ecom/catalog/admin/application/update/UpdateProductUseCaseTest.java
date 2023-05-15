package com.ecom.catalog.admin.application.update;

import com.ecom.catalog.admin.domain.product.Money;
import com.ecom.catalog.admin.domain.product.Product;
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
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @InjectMocks
    private DefaultUpdateProductUseCase useCase;
    @Mock
    private ProductGateway productGateway;

    @Test
    public void givenAValidCommand_whenCallsUpdateProduct_shouldReturnProductId() {
        // given
        final var aProduct =
                Product.newProduct("Celular A", "Descrição A", ProductStatus.ACTIVE, Money.with(10.0), 10);

        final var expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedId = aProduct.getId();

        final var aCommand = UpdateProductCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedStatus,
                expectedPrice,
                expectedStock
        );

        when(productGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Product.with(aProduct)));

        when(productGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(productGateway).findById(eq(expectedId));
        Mockito.verify(productGateway).update(Mockito.argThat(aUpdatedProduct ->
                        Objects.equals(expectedId, aUpdatedProduct.getId())
                        && Objects.equals(expectedName, aUpdatedProduct.getName())
                        && Objects.equals(expectedDescription, aUpdatedProduct.getDescription())
                        && Objects.equals(expectedPrice, aUpdatedProduct.getPrice())
                        && Objects.equals(expectedStock, aUpdatedProduct.getStock())
                        && Objects.equals(expectedStatus, aUpdatedProduct.getStatus())
                        && Objects.equals(aProduct.getCreatedAt(), aUpdatedProduct.getCreatedAt())
                        && aProduct.getUpdatedAt().isBefore(aUpdatedProduct.getUpdatedAt())
        ));
    }

}