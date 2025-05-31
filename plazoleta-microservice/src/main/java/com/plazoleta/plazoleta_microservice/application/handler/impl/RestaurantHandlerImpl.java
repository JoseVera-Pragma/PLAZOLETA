package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestauranteResumenResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final IRestauranteResumenResponseMapper restauranteResumenResponseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {

        Restaurant restaurant = restaurantRequestMapper.toRestaurant(restaurantRequestDto);

        restaurantServicePort.createRestaurant(restaurant);

        return restaurantResponseMapper.toRestaurantResponseDto(restaurant);
    }

    @Override
    public Page<RestaurantResumeResponseDto> restaurantList(int pageIndex, int elementsPerPage) {
        PageRequest pageable = PageRequest.of(pageIndex, elementsPerPage, Sort.by("name").ascending());

        Page<Restaurant> restaurantesPage = restaurantServicePort.findAll(pageable);

        List<RestaurantResumeResponseDto> dtos = restauranteResumenResponseMapper.toResumenDtoList(restaurantesPage.getContent());

        return new PageImpl<>(dtos, pageable, restaurantesPage.getTotalElements());
    }
}
