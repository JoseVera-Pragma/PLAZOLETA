package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto);
}
