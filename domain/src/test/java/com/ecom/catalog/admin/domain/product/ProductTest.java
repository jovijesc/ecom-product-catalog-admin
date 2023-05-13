package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    public void givenAValidParams_whenCallNewProduct_thenInstantiateProduct() {
        // given
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;

        // when
        final var actualProduct =
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock);

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertNotNull(actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertNotNull(actualProduct.getCreatedAt());
        Assertions.assertNotNull(actualProduct.getUpdatedAt());

    }

    @Test
    public void givenAnInvalidNullName_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = null;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = " ";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullDescription_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final String expectedDescription = null;
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyDescription_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final String expectedDescription = " ";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameWithLengthGreaterThan255_whenCallNewProduct_thenShouldReceiveAnError() {
        // given
        final String expectedName = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,
                """;
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidDescriptionWithLengthGreaterThan4000_whenCallsNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final String expectedDescription = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum
                """;
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' must be between 1 and 4000 characters";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidStock_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = -1;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'stock' cannot have invalid values";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullPrice_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final Money expectedPrice = null;
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidPriceWithAmountLessThan001_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final Money expectedPrice = Money.with(0);
        final var expectedStock = 10;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' must be greater than zero";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

}