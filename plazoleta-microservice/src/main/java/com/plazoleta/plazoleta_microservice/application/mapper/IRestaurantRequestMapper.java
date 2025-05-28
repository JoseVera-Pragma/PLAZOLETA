package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {IValueObjectMapper.class}
)
public interface IRestaurantRequestMapper {
    default Restaurant toRestaurant(RestaurantRequestDto dto) {
        return new Restaurant.Builder()
                .name(dto.getName())
                .nit(dto.getNit())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .urlLogo(dto.getUrlLogo())
                .idOwner(dto.getIdOwner())
                .build();
    }
    @Mapping(target = "name", source = "name")
    @Mapping(target = "nit", source = "nit")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    RestaurantRequestDto toRestaurantRequestDto(Restaurant restaurant);
}
