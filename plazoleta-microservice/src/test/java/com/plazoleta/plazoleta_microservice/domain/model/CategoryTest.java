package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.category.InvalidCategoryDataException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class CategoryTest {

    @Test
    void testValidCategoryCreation() {
        Long id = 1L;
        String name = "Comida";
        String description = "Categoría de comida";

        Category category = new Category(id, name, description);

        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }

    @Test
    void testNullNameThrowsException() {
        Exception exception = assertThrows(InvalidCategoryDataException.class, () -> {
            new Category(1L, null, "Descripción válida");
        });
        assertEquals("All category fields are mandatory and valid.", exception.getMessage());
    }

    @Test
    void testEmptyNameThrowsException() {
        Exception exception = assertThrows(InvalidCategoryDataException.class, () -> {
            new Category(1L, "", "Descripción válida");
        });
        assertEquals("All category fields are mandatory and valid.", exception.getMessage());
    }

    @Test
    void testBlankNameThrowsException() {
        Exception exception = assertThrows(InvalidCategoryDataException.class, () -> {
            new Category(1L, "   ", "Descripción válida");
        });
        assertEquals("All category fields are mandatory and valid.", exception.getMessage());
    }

    @Test
    void testNullDescriptionThrowsException() {
        Exception exception = assertThrows(InvalidCategoryDataException.class, () -> {
            new Category(1L, "Nombre válido", null);
        });
        assertEquals("All category fields are mandatory and valid.", exception.getMessage());
    }

    @Test
    void testEmptyDescriptionThrowsException() {
        Exception exception = assertThrows(InvalidCategoryDataException.class, () -> {
            new Category(1L, "Nombre válido", "");
        });
        assertEquals("All category fields are mandatory and valid.", exception.getMessage());
    }

    @Test
    void testBlankDescriptionThrowsException() {
        Exception exception = assertThrows(InvalidCategoryDataException.class, () -> {
            new Category(1L, "Nombre válido", "   ");
        });
        assertEquals("All category fields are mandatory and valid.", exception.getMessage());
    }
}