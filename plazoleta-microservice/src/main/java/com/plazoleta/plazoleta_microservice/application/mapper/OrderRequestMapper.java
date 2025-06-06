package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishOrderRequestDto;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;

import java.util.List;

public class OrderRequestMapper {
    public Order toDomain(CreateOrderRequestDto dto) {
        List<OrderDish> dishes = dto.getDishes()
                .stream()
                .map(this::toOrderDish)
                .toList();

        return Order.builder()
                .restaurantId(dto.getIdRestaurant())
                .dishes(dishes)
                .build();
    }

    private OrderDish toOrderDish(DishOrderRequestDto dto) {
        return new OrderDish(dto.getIdDish(), dto.getQuantity());
    }
}
