package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testBuilderAndGetters() {
        Long expectedId = 1L;
        Long expectedCustomerId = 10L;
        LocalDateTime expectedDate = LocalDateTime.now();
        OrderStatus expectedStatus = OrderStatus.PENDING;
        Long expectedChefId = 20L;
        Long expectedRestaurantId = 30L;

        OrderDish dish1 = new OrderDish(100L, 2);
        OrderDish dish2 = new OrderDish(101L, 1);

        Order order = Order.builder()
                .id(expectedId)
                .customerId(expectedCustomerId)
                .orderDate(expectedDate)
                .status(expectedStatus)
                .chefId(expectedChefId)
                .restaurantId(expectedRestaurantId)
                .dishes(Arrays.asList(dish1, dish2))
                .build();

        assertEquals(expectedId, order.getId());
        assertEquals(expectedCustomerId, order.getCustomerId());
        assertEquals(expectedDate, order.getOrderDate());
        assertEquals(expectedStatus, order.getStatus());
        assertEquals(expectedChefId, order.getChefId());
        assertEquals(expectedRestaurantId, order.getRestaurantId());
        assertNotNull(order.getDishes());
        assertEquals(2, order.getDishes().size());
    }

    @Test
    void testWithCustomerId() {
        Order order = Order.builder().customerId(1L).build();

        Order updated = order.withCustomerId(2L);

        assertEquals(2L, updated.getCustomerId());
        assertEquals(order.getId(), updated.getId());
    }

    @Test
    void testWithChefId() {
        Order order = Order.builder().chefId(1L).build();

        Order updated = order.withChefId(3L);

        assertEquals(3L, updated.getChefId());
        assertEquals(order.getId(), updated.getId());
    }

    @Test
    void testWithOrderDate() {
        LocalDateTime date1 = LocalDateTime.of(2025, 6, 5, 12, 0);
        LocalDateTime date2 = LocalDateTime.of(2025, 6, 6, 13, 30);

        Order order = Order.builder().orderDate(date1).build();

        Order updated = order.withOrderDate(date2);

        assertEquals(date2, updated.getOrderDate());
        assertEquals(order.getId(), updated.getId());
    }

    @Test
    void testWithStatus() {
        Order order = Order.builder().status(OrderStatus.PENDING).build();

        Order updated = order.withStatus(OrderStatus.READY);

        assertEquals(OrderStatus.READY, updated.getStatus());
        assertEquals(order.getId(), updated.getId());
    }
}