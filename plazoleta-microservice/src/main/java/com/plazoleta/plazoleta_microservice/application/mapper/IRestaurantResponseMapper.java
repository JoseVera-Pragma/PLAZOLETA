package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = false),
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {IValueObjectMapper.class}
)
public interface IRestaurantResponseMapper {
    @Mapping(target = "name", source = "name")
    @Mapping(target = "nit", source = "nit")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    Restaurant toRestaurant(RestaurantResponseDto dto);


    @Mapping(target = "name", source = "name")
    @Mapping(target = "nit", source = "nit")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    RestaurantResponseDto toRestaurantResponseDto(Restaurant restaurant);
}
