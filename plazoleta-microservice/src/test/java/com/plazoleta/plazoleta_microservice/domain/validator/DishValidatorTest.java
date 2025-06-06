package com.plazoleta.plazoleta_microservice.domain.validator;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.InvalidDishDataException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;

import static org.junit.jupiter.api.Assertions.*;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

class DishValidatorTest {

    private static final Category VALID_CATEGORY = new Category(1L, "Fast Food","Fast Food");

    private Dish buildValidDish() {
        return Dish.builder()
                .id(1L)
                .name("Pizza")
                .price(15000.0)
                .description("Delicious cheese pizza")
                .imageUrl("http://image.com/pizza.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(10L)
                .active(true)
                .build();
    }

    @Test
    void constructorShouldBePrivateAndInvokableViaReflection() throws Exception {
        Constructor<DishValidator> constructor = DishValidator.class.getDeclaredConstructor();
        assertFalse(constructor.canAccess(null) || constructor.isAccessible(), "Constructor should be private");

        constructor.setAccessible(true);
        DishValidator instance = constructor.newInstance();
        assertNotNull(instance);
    }
    
    @Test
    void validate_shouldPassWithValidDish() {
        assertDoesNotThrow(() -> DishValidator.validate(buildValidDish()));
    }

    @Test
    void validate_shouldThrowIfDishIsNull() {
        InvalidDishDataException ex = assertThrows(
                InvalidDishDataException.class,
                () -> DishValidator.validate(null)
        );
        assertEquals("Dish cannot be null.", ex.getMessage());
    }

    @Test
    void validate_shouldThrowIfNameIsNullOrBlank() {
        Dish nullName = Dish.builder()
                .id(1L)
                .name(null)
                .price(12000.0)
                .description("Desc")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        Dish blankName = Dish.builder()
                .id(1L)
                .name("  ")
                .price(12000.0)
                .description("Desc")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(nullName));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(blankName));
    }

    @Test
    void validate_shouldThrowIfPriceIsNullOrNonPositive() {
        Dish nullPrice = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(null)
                .description("Desc")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        Dish zeroPrice = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(0.0)
                .description("Desc")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        Dish negativePrice = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(-1.0)
                .description("Desc")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(nullPrice));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(zeroPrice));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(negativePrice));
    }

    @Test
    void validate_shouldThrowIfDescriptionIsNullOrBlank() {
        Dish nullDesc = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description(null)
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        Dish blankDesc = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description(" ")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(nullDesc));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(blankDesc));
    }

    @Test
    void validate_shouldThrowIfImageUrlIsNullOrBlank() {
        Dish nullImg = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("desc")
                .imageUrl(null)
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        Dish blankImg = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("desc")
                .imageUrl("   ")
                .category(VALID_CATEGORY)
                .restaurantId(1L)
                .active(true)
                .build();

        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(nullImg));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(blankImg));
    }

    @Test
    void validate_shouldThrowIfCategoryIsNullOrNameIsNullOrBlank() {
        Dish nullCategory = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("desc")
                .imageUrl("img.jpg")
                .category(null)
                .restaurantId(1L)
                .active(true)
                .build();

        Dish nullCategoryName = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("desc")
                .imageUrl("img.jpg")
                .category(new Category(1L, null,""))
                .restaurantId(1L)
                .active(true)
                .build();

        Dish blankCategoryName = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("desc")
                .imageUrl("img.jpg")
                .category(new Category(1L, " ",""))
                .restaurantId(1L)
                .active(true)
                .build();

        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(nullCategory));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(nullCategoryName));
        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(blankCategoryName));
    }

    @Test
    void validate_shouldThrowIfRestaurantIdIsNull() {
        Dish noRestaurant = Dish.builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("desc")
                .imageUrl("img.jpg")
                .category(VALID_CATEGORY)
                .restaurantId(null)
                .active(true)
                .build();

        assertThrows(InvalidDishDataException.class, () -> DishValidator.validate(noRestaurant));
    }
}