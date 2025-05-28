package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantHandlerImplTest {
    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    @InjectMocks
    private RestaurantHandlerImpl restaurantHandler;

    private RestaurantRequestDto requestDto;
    private Restaurant model;
    private RestaurantResponseDto responseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = RestaurantRequestDto.builder()
                .name("Pizza House")
                .nit("987654321")
                .address("123 Main St")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.com")
                .idOwner(1L)
                .build();

        model = new Restaurant.Builder()
                .name("Pizza House")
                .nit("987654321")
                .address("123 Main St")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.com")
                .idOwner(1L)
                .build();

        responseDto = RestaurantResponseDto.builder()
                .name("Pizza House")
                .nit("987654321")
                .address("123 Main St")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.com").build();
    }

    @Test
    void createRestaurantShouldReturnResponseDto() {
        when(restaurantRequestMapper.toRestaurant(requestDto)).thenReturn(model);
        doNothing().when(restaurantServicePort).createRestaurant(model);
        when(restaurantResponseMapper.toRestaurantResponseDto(model)).thenReturn(responseDto);

        RestaurantResponseDto result = restaurantHandler.createRestaurant(requestDto);

        assertNotNull(result);
        assertEquals("Pizza House", result.getName());
        assertEquals("987654321", result.getNit());

        verify(restaurantRequestMapper).toRestaurant(requestDto);
        verify(restaurantServicePort).createRestaurant(model);
        verify(restaurantResponseMapper).toRestaurantResponseDto(model);
    }
}