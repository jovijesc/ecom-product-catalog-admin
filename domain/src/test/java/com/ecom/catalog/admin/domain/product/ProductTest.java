package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.UnitTest;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.exceptions.NotificationException;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import com.ecom.catalog.admin.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class ProductTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallNewProduct_thenInstantiateProduct() {
        // given
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final String expectedName = "Celular";
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));

        // when
        final var actualProduct =
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertNotNull(actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategoryId, actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertNotNull(actualProduct.getCreatedAt());
        Assertions.assertNotNull(actualProduct.getUpdatedAt());

    }

    @Test
    public void givenAnInvalidNullName_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = null;
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = " ";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullDescription_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final String expectedDescription = null;
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyDescription_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final String expectedDescription = " ";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameWithLengthGreaterThan255_whenCallNewProduct_thenShouldReceiveAnError() {
        // given
        final String expectedName = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,
                """;
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidDescriptionWithLengthGreaterThan4000_whenCallsNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
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
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' must be between 1 and 4000 characters";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidStock_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = -1;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'stock' cannot have invalid values";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullPrice_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final Money expectedPrice = null;
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidPriceWithAmountLessThan001_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final Money expectedPrice = Money.with(0);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' must be greater than zero";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullCategory_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final Money expectedPrice = Money.with(1600.0);
        final var expectedStock = 10;
        final CategoryID categoryID = null;
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'category' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, categoryID, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullStore_whenCallNewProduct_thenShouldReceiverError() {
        // given
        final String expectedName = "Celular";
        final Store expectedStore = null;
        final var expectedDescription = "Celular do tipo ABC";
        final Money expectedPrice = Money.with(1600.0);
        final var expectedStock = 10;
        final CategoryID categoryID = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'store' should not be null";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, categoryID, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }


    @Test
    public void givenATotalImagesGreaterThan10_whenCallNewProduct_thenShouldReceiveAnError() {
        // given
        final var expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(
                ProductImage.with("qwe",new byte[]{10,20,30,40,50}, "image1.jpg", "/image", true),
                ProductImage.with("wer",new byte[]{10,20,30,40,50}, "image2.jpg", "/image", true),
                ProductImage.with("ert",new byte[]{10,20,30,40,50}, "image3.jpg", "/image", true),
                ProductImage.with("rty",new byte[]{10,20,30,40,50}, "image4.jpg", "/image", true),
                ProductImage.with("tyu",new byte[]{10,20,30,40,50}, "image5.jpg", "/image", true),
                ProductImage.with("yui",new byte[]{10,20,30,40,50}, "image6.jpg", "/image", true),
                ProductImage.with("uio",new byte[]{10,20,30,40,50}, "image7.jpg", "/image", true),
                ProductImage.with("iop",new byte[]{10,20,30,40,50}, "image8.jpg", "/image", true),
                ProductImage.with("asd",new byte[]{10,20,30,40,50}, "image9.jpg", "/image", true),
                ProductImage.with("sdf",new byte[]{10,20,30,40,50}, "image10.jpg", "/image", true),
                ProductImage.with("dfg",new byte[]{10,20,30,40,50}, "image11.jpg", "/image", true)
        );
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "The total number of images must be between 1 and 10";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValidActiveProduct_whenCallDeactivate_thenReturnProductInactivated() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));

        final var aProduct =
                Product.newProduct(expectedName, expectedDescription, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        final var createdAt = aProduct.getCreatedAt();
        final var updatedAt = aProduct.getUpdatedAt();

        // when
        final var actualProduct = aProduct.deactivate();

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertNotNull(actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategoryId, actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(createdAt, actualProduct.getCreatedAt());
        Assertions.assertTrue(actualProduct.getUpdatedAt().isAfter(updatedAt));

    }

    @Test
    public void givenAValidInactiveProduct_whenCallActivate_thenReturnProductActivated() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));

        final var aProduct =
                Product.newProduct(expectedName, expectedDescription, ProductStatus.INACTIVE, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        final var createdAt = aProduct.getCreatedAt();
        final var updatedAt = aProduct.getUpdatedAt();

        Assertions.assertEquals(aProduct.getStatus(), ProductStatus.INACTIVE);

        // when
        final var actualProduct = aProduct.activate();

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertNotNull(actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategoryId, actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(createdAt, actualProduct.getCreatedAt());
        Assertions.assertTrue(actualProduct.getUpdatedAt().isAfter(updatedAt));

    }

    @Test
    public void givenAValidProduct_whenCallUpdate_thenReturnProductUpdated() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));

        final var actualProduct =
                Product.newProduct("Celular Novo", "Celular com outra descrição", ProductStatus.INACTIVE, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        final var createdAt = actualProduct.getCreatedAt();
        final var updatedAt = actualProduct.getUpdatedAt();

        Assertions.assertEquals(actualProduct.getStatus(), ProductStatus.INACTIVE);

        // when
        actualProduct.update(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertNotNull(actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategoryId, actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(createdAt, actualProduct.getCreatedAt());
        Assertions.assertTrue(actualProduct.getUpdatedAt().isAfter(updatedAt));
    }

    @Test
    public void givenAValidProduct_whenCallUpdateToInactive_thenReturnProductUpdated() {
        // given
        final String expectedName = "Celular";
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.INACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));

        final var actualProduct =
                Product.newProduct("Celular Novo", "Celular com outra descrição", ProductStatus.ACTIVE, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        final var createdAt = actualProduct.getCreatedAt();
        final var updatedAt = actualProduct.getUpdatedAt();

        Assertions.assertEquals(actualProduct.getStatus(), ProductStatus.ACTIVE);

        // when
        actualProduct.update(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        // then
        Assertions.assertNotNull(actualProduct);;
        Assertions.assertNotNull(actualProduct.getId());
        Assertions.assertEquals(expectedName, actualProduct.getName());
        Assertions.assertEquals(expectedDescription, actualProduct.getDescription());
        Assertions.assertEquals(expectedPrice, actualProduct.getPrice());
        Assertions.assertEquals(expectedStock, actualProduct.getStock());
        Assertions.assertEquals(expectedStatus, actualProduct.getStatus());
        Assertions.assertEquals(expectedCategoryId, actualProduct.getCategoryId());
        Assertions.assertEquals(expectedStore, actualProduct.getStore());
        Assertions.assertEquals(createdAt, actualProduct.getCreatedAt());
        Assertions.assertTrue(actualProduct.getUpdatedAt().isAfter(updatedAt));

    }

    @Test
    public void givenAnInvalidNullName_whenCallUpdate_thenShouldReceiverError() {
        // given
        final String expectedName = null;
        final var expectedStore = Store.with(IdUtils.uuid(), "Minha Loja");
        final var expectedDescription = "Celular do tipo ABC";
        final var expectedPrice = Money.with(1800.03);
        final var expectedStock = 10;
        final var expectedStatus = ProductStatus.ACTIVE;
        final var expectedCategoryId = CategoryID.from("123");
        final var expectedImages = Set.of(ProductImage.with("123", new byte[]{10,20,30,40,50},"image.jpg", "/image", true));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualProduct =
                Product.newProduct("Celular Novo", "Celular com outra descrição", expectedStatus, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                actualProduct.update(expectedName, expectedDescription, expectedStatus, expectedPrice, expectedStock, expectedCategoryId, expectedStore, expectedImages));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

}