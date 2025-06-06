package com.plazoleta.plazoleta_microservice.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class DishTest {

    @Test
    void testBuilderAndGetters() {
        Category category = new Category(1L, "CategoryName", "CategoryDescription");

        Dish dish = Dish.builder()
                .id(10L)
                .name("Pizza")
                .price(12.5)
                .description("Delicious pizza")
                .imageUrl("http://image.url/pizza.jpg")
                .category(category)
                .active(false)
                .restaurantId(5L)
                .build();

        assertEquals(10L, dish.getId());
        assertEquals("Pizza", dish.getName());
        assertEquals(12.5, dish.getPrice());
        assertEquals("Delicious pizza", dish.getDescription());
        assertEquals("http://image.url/pizza.jpg", dish.getImageUrl());
        assertEquals(category, dish.getCategory());
        assertFalse(dish.isActive());
        assertEquals(5L, dish.getRestaurantId());
    }

    @Test
    void testActivate() {
        Dish dish = Dish.builder()
                .active(false)
                .build();

        Dish activated = dish.activate();

        assertTrue(activated.isActive());

        Dish alreadyActive = activated.activate();
        assertSame(activated, alreadyActive);
    }

    @Test
    void testDeactivate() {
        Dish dish = Dish.builder()
                .active(true)
                .build();

        Dish deactivated = dish.deactivate();

        assertFalse(deactivated.isActive());

        Dish alreadyInactive = deactivated.deactivate();
        assertSame(deactivated, alreadyInactive);
    }

    @Test
    void testWithCategory() {
        Category oldCategory = new Category(1L, "Old", "Old desc");
        Category newCategory = new Category(2L, "New", "New desc");

        Dish dish = Dish.builder()
                .category(oldCategory)
                .build();

        Dish updatedDish = dish.withCategory(newCategory);

        assertEquals(newCategory, updatedDish.getCategory());
        assertEquals(dish.getId(), updatedDish.getId());
        assertEquals(dish.getName(), updatedDish.getName());
    }

    @Test
    void testWithPriceAndDescription() {
        Dish dish = Dish.builder()
                .price(10.0)
                .description("Old desc")
                .build();

        Dish updatedDish = dish.withPriceAndDescription(15.0, "New desc");

        assertEquals(15.0, updatedDish.getPrice());
        assertEquals("New desc", updatedDish.getDescription());
        assertEquals(dish.getId(), updatedDish.getId());
        assertEquals(dish.getName(), updatedDish.getName());
    }
}