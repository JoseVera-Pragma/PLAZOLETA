package com.plazoleta.plazoleta_microservice.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.plazoleta.plazoleta_microservice.domain.validator.DishValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class DishTest {

    private Category category;

    @BeforeEach
    void setup() {
        category = new Category(1L, "Postres", "Dulces deliciosos");
    }

    @Test
    void testBuildValidDish() {
        try (MockedStatic<DishValidator> validatorMock = Mockito.mockStatic(DishValidator.class)) {
            validatorMock.when(() -> DishValidator.validate(any(), any(), any(), any(), any(), any())).thenCallRealMethod();;

            Dish dish = Dish.builder()
                    .id(10L)
                    .name("Tarta de chocolate")
                    .price(15.5)
                    .description("Deliciosa tarta")
                    .imageUrl("http://image.url/tarta.jpg")
                    .category(category)
                    .restaurantId(100L)
                    .active(false)
                    .build();

            assertEquals(10L, dish.getId());
            assertEquals("Tarta de chocolate", dish.getName());
            assertEquals(15.5, dish.getPrice());
            assertEquals("Deliciosa tarta", dish.getDescription());
            assertEquals("http://image.url/tarta.jpg", dish.getImageUrl());
            assertEquals(category, dish.getCategory());
            assertEquals(100L, dish.getRestaurantId());
            assertFalse(dish.isActive());
        }
    }

    @Test
    void testBuildInvalidDishThrowsException() {
        try (MockedStatic<DishValidator> validatorMock = Mockito.mockStatic(DishValidator.class)) {
            validatorMock.when(() -> DishValidator.validate(any(), any(), any(), any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Datos inválidos"));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                Dish.builder()
                        .name(null)
                        .price(-10.0)
                        .description("")
                        .imageUrl(null)
                        .category(null)
                        .restaurantId(null)
                        .build();
            });

            assertEquals("Datos inválidos", ex.getMessage());
        }
    }

    @Test
    void testActivateDeactiveToggle() {
        try (MockedStatic<DishValidator> validatorMock = Mockito.mockStatic(DishValidator.class)) {
            validatorMock.when(() -> DishValidator.validate(any(), any(), any(), any(), any(), any())).thenCallRealMethod();

            Dish dish = Dish.builder()
                    .name("Pizza")
                    .price(20.0)
                    .description("Pizza grande")
                    .imageUrl("url")
                    .category(category)
                    .restaurantId(1L)
                    .active(false)
                    .build();

            Dish activatedDish = dish.activate();
            assertTrue(activatedDish.isActive());
            assertEquals(dish.getId(), activatedDish.getId());
            assertEquals(dish.getName(), activatedDish.getName());

            Dish sameActivated = activatedDish.activate();
            assertSame(activatedDish, sameActivated);

            Dish deactivatedDish = activatedDish.deactivate();
            assertFalse(deactivatedDish.isActive());

            Dish sameDeactivated = deactivatedDish.deactivate();
            assertSame(deactivatedDish, sameDeactivated);
        }
    }
}
