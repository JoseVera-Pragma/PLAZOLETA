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
    void testAssignOrderId(){
        Long expectedOrderId = 1L;
        Long expectedDishId = 1L;
        Integer expectedQuantity = 3;

        OrderDish orderDish = new OrderDish(expectedDishId, expectedQuantity);

        orderDish.assignOrderId(1L);

        assertEquals(expectedOrderId, orderDish.getOrderId());
    }

}