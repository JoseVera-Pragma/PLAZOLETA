package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

import java.util.List;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto);
    Page<RestaurantResumeResponseDto> restaurantPage(int pageIndex, int elementsPerPage);
}
