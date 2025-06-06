package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResumeResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandlerImpl implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IRestaurantResumeResponseMapper restaurantResumeResponseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        Restaurant restaurant = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        Restaurant restaurantResponse = restaurantServicePort.createRestaurant(restaurant);
        return restaurantResponseMapper.toRestaurantResponseDto(restaurantResponse);
    }

    @Override
    public List<RestaurantResumeResponseDto> restaurantList(int pageIndex, int elementsPerPage) {
        List<Restaurant> restaurantsPage = restaurantServicePort.findAllRestaurants(pageIndex,elementsPerPage);
        return restaurantResumeResponseMapper.toResumeDtoList(restaurantsPage);
    }
}
