package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IRestauranteResumenResponseMapper {
    RestaurantResumeResponseDto toResumenDto(Restaurant restaurant);
    List<RestaurantResumeResponseDto> toResumenDtoList(List<Restaurant> restaurantList);
}
