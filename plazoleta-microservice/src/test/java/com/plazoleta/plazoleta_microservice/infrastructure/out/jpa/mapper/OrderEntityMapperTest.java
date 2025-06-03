package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderDishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OrderEntityMapperTest {

    private IDishEntityMapper dishEntityMapper;
    private OrderEntityMapper orderEntityMapper;

    @BeforeEach
    void setup() {
        dishEntityMapper = mock(IDishEntityMapper.class);
        orderEntityMapper = new OrderEntityMapper();
    }


    @Test
    void toDomain_shouldMapOrderEntityToOrderDomain() {
        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(1L);

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        orderDishEntity.setDish(dishEntity);
        orderDishEntity.setQuantity(3);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(10L);
        orderEntity.setCustomerId(100L);
        orderEntity.setChefId(200L);
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setStatus(OrderStatus.PENDING);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(300L);
        orderEntity.setRestaurant(restaurantEntity);

        orderEntity.setDishes(List.of(orderDishEntity));

        Order order = orderEntityMapper.toDomain(orderEntity);

        assertEquals(orderEntity.getId(), order.getId());
        assertEquals(orderEntity.getCustomerId(), order.getCustomerId());
        assertEquals(orderEntity.getChefId(), order.getChefId());
        assertEquals(orderEntity.getOrderDate(), order.getOrderDate());
        assertEquals(orderEntity.getStatus(), order.getStatus());
        assertEquals(restaurantEntity.getId(), order.getRestaurantId());

        assertNotNull(order.getDishes());
        assertEquals(1, order.getDishes().size());
        assertEquals(dishEntity.getId(), order.getDishes().get(0).getDishId());
        assertEquals(orderDishEntity.getQuantity(), order.getDishes().get(0).getQuantity());
    }

    @Test
    void toEntity_shouldMapOrderDomainToOrderEntity() {
        OrderDish domainDish = new OrderDish(1L, 2);
        Order orderDomain = new Order(
                10L,
                100L,
                200L,
                300L,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                List.of(domainDish)
        );

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(300L);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(1L);

        List<DishEntity> dishEntities = List.of(dishEntity);

        OrderEntity orderEntity = orderEntityMapper.toEntity(orderDomain, restaurantEntity, dishEntities);

        assertEquals(orderDomain.getId(), orderEntity.getId());
        assertEquals(orderDomain.getCustomerId(), orderEntity.getCustomerId());
        assertEquals(orderDomain.getChefId(), orderEntity.getChefId());
        assertEquals(orderDomain.getOrderDate(), orderEntity.getOrderDate());
        assertEquals(orderDomain.getStatus(), orderEntity.getStatus());
        assertEquals(restaurantEntity, orderEntity.getRestaurant());

        assertNotNull(orderEntity.getDishes());
        assertEquals(1, orderEntity.getDishes().size());

        OrderDishEntity dishEntityMapped = orderEntity.getDishes().get(0);
        assertEquals(dishEntity, dishEntityMapped.getDish());
        assertEquals(orderEntity, dishEntityMapped.getOrder());
        assertEquals(domainDish.getQuantity(), dishEntityMapped.getQuantity());
    }

    @Test
    void toEntity_shouldThrowExceptionIfDishNotFound() {
        OrderDish domainDish = new OrderDish(1L, 2);
        Order orderDomain = new Order(
                10L,
                100L,
                200L,
                300L,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                List.of(domainDish)
        );

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(300L);

        List<DishEntity> emptyDishEntities = List.of();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderEntityMapper.toEntity(orderDomain, restaurantEntity, emptyDishEntities);
        });

        assertTrue(exception.getMessage().contains("Dish not found"));
    }
}