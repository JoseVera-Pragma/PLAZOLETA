package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "dishes", target = "dishes")
    Order toOrder(OrderEntity orderEntity);

    @Mapping(source = "restaurantId", target = "restaurant.id")
    @Mapping(source = "dishes", target = "dishes")
    OrderEntity toOrderEntity(Order order);

    List<Order> toOrderList(List<OrderEntity> orderEntities);

    List<OrderEntity> toOrderEntityList(List<Order> orders);
}
