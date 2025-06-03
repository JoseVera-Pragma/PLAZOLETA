package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;

import java.util.List;

public class OrderRequestMapper {
    public Order toDomain(CreateOrderRequestDto dto) {
        List<OrderDish> dishes = dto.getDishes()
                .stream()
                .map(d -> new OrderDish(d.getIdDish(), d.getQuantity()))
                .toList();

        return new Order(
                null,
                null,
                null,
                dto.getIdRestaurant(),
                null,
                null,
                dishes
        );
    }
}
