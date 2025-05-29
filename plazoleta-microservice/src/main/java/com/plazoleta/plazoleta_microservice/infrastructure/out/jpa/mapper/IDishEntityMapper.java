package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishEntityMapper {
    @Mapping(source = "category", target = "category")
    @Mapping(source = "restaurantId", target = "restaurantId.id")
    DishEntity toEntity(Dish dish);

    @Mapping(source = "restaurantId.id", target = "restaurantId")
    Dish toModel(DishEntity entity);

    DishResponseDto toResponseDto(Dish dish);

    Dish toModel(DishRequestDto dto);
}
