package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.UnitTest;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductImageTest extends UnitTest {

    @Test
    public void givenValidParams_whenCallsNewImage_shouldReturnInstance() {
        // given
        final var expectedCheckSum = "abc";
        final var expectedName = "Banner.png";
        final var expectedLocation = "/image/ac";
        final var expectedOrder = 1;
        final var expectedFeatured = true;

        // when
        final var actualImage =
                ProductImage.with(expectedCheckSum, expectedName, expectedLocation, expectedOrder, expectedFeatured);

        // then
        Assertions.assertNotNull(actualImage);
        Assertions.assertEquals(expectedCheckSum, actualImage.getChecksum());
        Assertions.assertEquals(expectedName, actualImage.getName());
        Assertions.assertEquals(expectedLocation, actualImage.getLocation());
        Assertions.assertEquals(expectedOrder, actualImage.getOrder());
        Assertions.assertEquals(expectedFeatured, actualImage.isFeatured());
    }

    @Test
    public void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_shouldReturnTrue() {
        // given
        final var expectedCheckSum = "abc";
        final var expectedLocation = "/image/ac";
        final var expectedOrder = 1;
        final var expectedFeatured = true;

        final var image01 =
                ProductImage.with(expectedCheckSum, "Imagem 01", expectedLocation, expectedOrder, expectedFeatured);

        final var image02 =
                ProductImage.with(expectedCheckSum, "Imagem 02", expectedLocation, expectedOrder, expectedFeatured);

        // then
        Assertions.assertEquals(image01, image02);
        Assertions.assertNotSame(image01, image02);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_shouldReturnError() {
        final var expectedName = "image.jpg";
        final var expectedLocation = "/image/ac";
        final var expectedChecksum = "abc";
        final var expectedOrder = 1;
        final var expectedFeatured = true;
        final var expectedErrorCount = 1;

        var actualException = Assertions.assertThrows(NotificationException.class, () ->
                ProductImage.with(expectedChecksum, null, expectedLocation, expectedOrder, expectedFeatured));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals("'name' should not be null", actualException.getErrors().get(0).message());

        actualException = Assertions.assertThrows(NotificationException.class, () ->
                ProductImage.with(expectedChecksum, " ", expectedLocation, expectedOrder, expectedFeatured));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals("'name' should not be empty", actualException.getErrors().get(0).message());


        actualException = Assertions.assertThrows(NotificationException.class, () ->
                ProductImage.with(expectedChecksum, expectedName, null, expectedOrder, expectedFeatured));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals("'location' should not be null", actualException.getErrors().get(0).message());

        actualException = Assertions.assertThrows(NotificationException.class, () ->
                ProductImage.with(expectedChecksum, expectedName, " ", expectedOrder, expectedFeatured));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals("'location' should not be empty", actualException.getErrors().get(0).message());

        actualException = Assertions.assertThrows(NotificationException.class, () ->
                ProductImage.with(null, "image.jpg", expectedLocation, expectedOrder, expectedFeatured));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals("'checksum' should not be null", actualException.getErrors().get(0).message());

        actualException = Assertions.assertThrows(NotificationException.class, () ->
                ProductImage.with(" ", "image.jpg", expectedLocation, expectedOrder, expectedFeatured));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals("'checksum' should not be empty", actualException.getErrors().get(0).message());

    }

}