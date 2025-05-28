package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.application.mapper.IValueObjectMapper;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = false),
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {IValueObjectMapper.class})
public interface IRestaurantEntityMapper {
    @Mapping(target = "name", source = "name")
    @Mapping(target = "nit", source = "nit")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    RestaurantEntity toRestaurantEntity(Restaurant restaurant);


    @Mapping(target = "name", source = "name")
    @Mapping(target = "nit", source = "nit")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    Restaurant toRestaurant(RestaurantEntity restaurantEntity);
}
