package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testConstructorAndGetters() {
        Long expectedId = 100L;
        Long expectedCustomerId = 200L;
        Long expectedChefId = 300L;
        Long expectedRestaurantId = 400L;
        LocalDateTime expectedOrderDate = LocalDateTime.now();
        OrderStatus expectedStatus = OrderStatus.PENDING;

        OrderDish dish1 = new OrderDish(1L, 2);
        OrderDish dish2 = new OrderDish(2L, 3);
        List<OrderDish> expectedDishes = List.of(dish1, dish2);

        Order order = new Order(
                expectedId,
                expectedCustomerId,
                expectedChefId,
                expectedRestaurantId,
                expectedOrderDate,
                expectedStatus,
                expectedDishes
        );

        assertEquals(expectedId, order.getId());
        assertEquals(expectedCustomerId, order.getCustomerId());
        assertEquals(expectedChefId, order.getChefId());
        assertEquals(expectedRestaurantId, order.getRestaurantId());
        assertEquals(expectedOrderDate, order.getOrderDate());
        assertEquals(expectedStatus, order.getStatus());
        assertEquals(expectedDishes, order.getDishes());
    }

    @Test
    void testSetters() {
        Order order = new Order(1L, 2L, 3L, 4L, LocalDateTime.now(), OrderStatus.PENDING, List.of());

        LocalDateTime newDate = LocalDateTime.now().plusHours(1);
        List<OrderDish> newDishes = List.of(new OrderDish(10L, 5));

        order.setId(10L);
        order.setCustomerId(20L);
        order.setChefId(30L);
        order.setRestaurantId(40L);
        order.setOrderDate(newDate);
        order.setStatus(OrderStatus.READY);
        order.setDishes(newDishes);

        assertEquals(10L, order.getId());
        assertEquals(20L, order.getCustomerId());
        assertEquals(30L, order.getChefId());
        assertEquals(40L, order.getRestaurantId());
        assertEquals(newDate, order.getOrderDate());
        assertEquals(OrderStatus.READY, order.getStatus());
        assertEquals(newDishes, order.getDishes());
    }

    @Test
    void testOrderStatusEnum() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals(OrderStatus.IN_PREPARATION, OrderStatus.valueOf("IN_PREPARATION"));
        assertEquals(OrderStatus.READY, OrderStatus.valueOf("READY"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
    }
}