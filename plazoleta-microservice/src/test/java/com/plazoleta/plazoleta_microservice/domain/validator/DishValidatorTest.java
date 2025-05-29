package com.plazoleta.plazoleta_microservice.domain.validator;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.InvalidDishDataException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class DishValidatorTest {

    private Category validCategory = new Category(1L, "Postre", "Categoría postres");

    @Test
    void validate_throwsIfNameIsNullOrBlank() {
        InvalidDishDataException ex1 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate(null, 10.0, "desc", "img.jpg", validCategory, 1L);
        });
        assertEquals("The name of the dish is mandatory.", ex1.getMessage());

        InvalidDishDataException ex2 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("  ", 10.0, "desc", "img.jpg", validCategory, 1L);
        });
        assertEquals("The name of the dish is mandatory.", ex2.getMessage());
    }

    @Test
    void validate_throwsIfPriceIsZeroOrNegative() {
        InvalidDishDataException ex1 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 0.0, "desc", "img.jpg", validCategory, 1L);
        });
        assertEquals("The price of the dish must be a positive integer greater than 0.", ex1.getMessage());

        InvalidDishDataException ex2 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", -5.0, "desc", "img.jpg", validCategory, 1L);
        });
        assertEquals("The price of the dish must be a positive integer greater than 0.", ex2.getMessage());
    }

    @Test
    void validate_throwsIfDescriptionIsNullOrBlank() {
        InvalidDishDataException ex1 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, null, "img.jpg", validCategory, 1L);
        });
        assertEquals("The description of the dish is mandatory.", ex1.getMessage());

        InvalidDishDataException ex2 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, "   ", "img.jpg", validCategory, 1L);
        });
        assertEquals("The description of the dish is mandatory.", ex2.getMessage());
    }

    @Test
    void validate_throwsIfImageUrlIsNullOrBlank() {
        InvalidDishDataException ex1 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, "desc", null, validCategory, 1L);
        });
        assertEquals("The URL of the image of the plate is mandatory.", ex1.getMessage());

        InvalidDishDataException ex2 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, "desc", "  ", validCategory, 1L);
        });
        assertEquals("The URL of the image of the plate is mandatory.", ex2.getMessage());
    }

    @Test
    void validate_throwsIfCategoryIsNullOrNameBlank() {
        InvalidDishDataException ex1 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, "desc", "img.jpg", null, 1L);
        });
        assertEquals("The plate category is mandatory.", ex1.getMessage());

        Category blankNameCategory = mock(Category.class);
        when(blankNameCategory.getName()).thenReturn("   ");

        InvalidDishDataException ex2 = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, "desc", "img.jpg", blankNameCategory, 1L);
        });
        assertEquals("The plate category is mandatory.", ex2.getMessage());
    }


    @Test
    void validate_throwsIfRestaurantIdIsNull() {
        InvalidDishDataException ex = assertThrows(InvalidDishDataException.class, () -> {
            DishValidator.validate("Name", 10.0, "desc", "img.jpg", validCategory, null);
        });
        assertEquals("The dish must be associated with a restaurant.", ex.getMessage());
    }

    @Test
    void validate_passesWithValidData() {
        assertDoesNotThrow(() -> {
            DishValidator.validate("Name", 10.0, "desc", "img.jpg", validCategory, 1L);
        });
    }
}