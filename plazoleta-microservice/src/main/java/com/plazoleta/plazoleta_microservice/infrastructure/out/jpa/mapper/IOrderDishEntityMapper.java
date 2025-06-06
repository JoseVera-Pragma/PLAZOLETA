package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderDishEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderDishEntityMapper {

    @Mapping(target = "id.orderId", source = "orderId")
    @Mapping(target = "id.dishId", source = "dishId")
    @Mapping(target = "dish.id", source = "dishId")
    OrderDishEntity toEntity(OrderDish dish);

    @InheritInverseConfiguration
    OrderDish toDomain(OrderDishEntity entity);
}