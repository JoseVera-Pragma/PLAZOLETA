package com.plazoleta.plazoleta_microservice.application.mapper;


import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderResponseMapper {

    @Mapping(target = "statusDescription", ignore = true)
    @Mapping(target = "restaurantId", source = "restaurant.id")
    OrderResponseDto toResponseDto(Order order);

    List<OrderResponseDto> toResponsesDto(List<Order> orders);

    @AfterMapping
    default void mapStatusToDescription(Order order, @MappingTarget OrderResponseDto dto) {
        dto.setStatusDescription(getSpanishStatus(order.getStatus()));
    }

    default String getSpanishStatus(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Pendiente";
            case CANCELLED -> "Cancelado";
            case IN_PREPARATION -> "En preparación";
            case READY -> "Listo";
            case DELIVERED -> "Entregado";
        };
    }
}
