package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import org.springframework.data.domain.Page;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto);

    Page<RestaurantResumeResponseDto> restaurantList(int pageIndex, int elementsPerPage);
}
