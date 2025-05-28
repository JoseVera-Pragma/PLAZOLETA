package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.application.mapper.IValueObjectMapper;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {IValueObjectMapper.class})
public interface IRestaurantEntityMapper {
    @Mapping(target = "name", source = "name")
    @Mapping(target = "nit", source = "nit")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    RestaurantEntity toRestaurantEntity(Restaurant restaurant);


    default Restaurant toRestaurant(RestaurantEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Restaurant.Builder()
                .id(entity.getId())
                .name(entity.getName())
                .nit(entity.getNit())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .urlLogo(entity.getUrlLogo())
                .idOwner(entity.getIdOwner())
                .build();
    }
}
