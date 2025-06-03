package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderEntityMapper {
    private final IDishEntityMapper dishEntityMapper;

    public OrderEntityMapper(IDishEntityMapper dishEntityMapper) {
        this.dishEntityMapper = dishEntityMapper;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderDish> dishes = entity.getDishes().stream()
                .map(this::toDomainDish)
                .collect(Collectors.toList());

        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getChefId(),
                entity.getRestaurant().getId(),
                entity.getOrderDate(),
                entity.getStatus(),
                dishes
        );
    }

    public OrderEntity toEntity(Order domain, RestaurantEntity restaurantEntity, List<DishEntity> dishEntities) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setOrderDate(domain.getOrderDate());
        entity.setStatus(domain.getStatus());
        entity.setChefId(domain.getChefId());
        entity.setRestaurant(restaurantEntity);

        List<OrderDishEntity> dishEntitiesMapped = domain.getDishes().stream()
                .map(d -> toEntityDish(d, entity, findDishEntityById(dishEntities, d.getDishId())))
                .collect(Collectors.toList());

        entity.setDishes(dishEntitiesMapped);
        return entity;
    }

    private OrderDish toDomainDish(OrderDishEntity entity) {
        return new OrderDish(
                entity.getDish().getId(),
                entity.getQuantity()
        );
    }

    private OrderDishEntity toEntityDish(OrderDish domainDish, OrderEntity order, DishEntity dish) {
        OrderDishEntity entity = new OrderDishEntity();
        entity.setOrder(order);
        entity.setDish(dish);
        entity.setQuantity(domainDish.getQuantity());
        return entity;
    }

    private DishEntity findDishEntityById(List<DishEntity> dishEntities, Long dishId) {
        return dishEntities.stream()
                .filter(d -> d.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishId));
    }
}
