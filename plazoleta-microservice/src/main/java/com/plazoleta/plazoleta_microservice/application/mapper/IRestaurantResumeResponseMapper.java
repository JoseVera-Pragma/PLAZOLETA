package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IRestaurantResumeResponseMapper {
    RestaurantResumeResponseDto toResumeDto(Restaurant restaurant);
    List<RestaurantResumeResponseDto> toResumeDtoList(List<Restaurant> restaurantList);

    default Page<RestaurantResumeResponseDto> toResumeDtoPage(Page<Restaurant> restaurantPage) {
        List<RestaurantResumeResponseDto> dtoList = toResumeDtoList(restaurantPage.getContent());

        return new Page<>(
                dtoList,
                restaurantPage.getPageNumber(),
                restaurantPage.getPageSize(),
                restaurantPage.getTotalElements()
        );
    }
}
