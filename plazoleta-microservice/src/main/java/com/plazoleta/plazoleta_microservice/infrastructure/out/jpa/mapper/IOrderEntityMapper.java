package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderDishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = { IRestaurantEntityMapper.class })
public interface IOrderEntityMapper {

    @Mapping(target = "restaurant", source = "restaurant")
    OrderEntity toEntity(Order order);

    Order toDomain(OrderEntity entity);
}