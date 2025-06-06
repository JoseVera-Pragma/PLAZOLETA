package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderEntityMapper {

    public Order toOrder(OrderEntity orderEntity) {
        if (orderEntity == null) return null;

        List<OrderDish> dishes = new ArrayList<>();
        if (orderEntity.getDishes() != null) {
            for (OrderDishEntity dishEntity : orderEntity.getDishes()) {
                dishes.add(new OrderDish(dishEntity.getDish().getId(), dishEntity.getQuantity()));
            }
        }

        return Order.builder()
                .id(orderEntity.getId())
                .customerId(orderEntity.getCustomerId())
                .orderDate(orderEntity.getOrderDate())
                .status(orderEntity.getStatus())
                .chefId(orderEntity.getChefId())
                .restaurantId(orderEntity.getRestaurant() != null ? orderEntity.getRestaurant().getId() : null)
                .dishes(dishes)
                .securityPin(orderEntity.getSecurityPin())
                .build();
    }

    public OrderEntity toOrderEntity(Order order) {
        if (order == null) return null;

        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setOrderDate(order.getOrderDate());
        entity.setStatus(order.getStatus());
        entity.setChefId(order.getChefId());
        entity.setSecurityPin(order.getSecurityPin());

        if (order.getRestaurantId() != null) {
            RestaurantEntity restaurant = new RestaurantEntity();
            restaurant.setId(order.getRestaurantId());
            entity.setRestaurant(restaurant);
        }

        if (order.getDishes() != null) {
            List<OrderDishEntity> dishEntities = new ArrayList<>();
            for (OrderDish orderDish : order.getDishes()) {
                OrderDishEntity dishEntity = new OrderDishEntity();

                OrderDishId id = new OrderDishId(order.getId(), orderDish.getDishId());
                dishEntity.setId(id);
                dishEntity.setQuantity(orderDish.getQuantity());
                dishEntity.setOrder(entity);

                DishEntity dish = new DishEntity();
                dish.setId(orderDish.getDishId());
                dishEntity.setDish(dish);

                dishEntities.add(dishEntity);
            }
            entity.setDishes(dishEntities);
        }

        return entity;
    }
}