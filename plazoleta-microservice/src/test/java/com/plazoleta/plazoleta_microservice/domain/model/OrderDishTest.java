package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDishTest {

    @Test
    void testConstructorAndGetters() {
        Long expectedDishId = 1L;
        Integer expectedQuantity = 3;

        OrderDish orderDish = new OrderDish(expectedDishId, expectedQuantity);

        assertEquals(expectedDishId, orderDish.getDishId());
        assertEquals(expectedQuantity, orderDish.getQuantity());
    }

    @Test
    void testSetters() {
        OrderDish orderDish = new OrderDish(1L, 1);

        orderDish.setDishId(2L);
        orderDish.setQuantity(5);

        assertEquals(2L, orderDish.getDishId());
        assertEquals(5, orderDish.getQuantity());
    }

}