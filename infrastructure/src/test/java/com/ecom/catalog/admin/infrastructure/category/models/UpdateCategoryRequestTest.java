package com.ecom.catalog.admin.infrastructure.category.models;

import com.ecom.catalog.admin.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

@JacksonTest
public class UpdateCategoryRequestTest {

    @Autowired
    private JacksonTester<UpdateCategoryRequest> json;

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedName = "Eletronicos";
        final var expectedDescription = "Eletronicos do tipo abc";
        final var expectedIsActive = false;

        final var json = """
                {          
                  "name": "%s",
                  "description": "%s",
                  "is_active": %s         
                }    
                """.formatted(
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }
}
