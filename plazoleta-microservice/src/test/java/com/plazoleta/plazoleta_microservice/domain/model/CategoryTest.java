package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testCategoryConstructorAndGetters() {
        Long expectedId = 1L;
        String expectedName = "Beverages";
        String expectedDescription = "Drinks and refreshments";

        Category category = new Category(expectedId, expectedName, expectedDescription);

        assertEquals(expectedId, category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
    }
}