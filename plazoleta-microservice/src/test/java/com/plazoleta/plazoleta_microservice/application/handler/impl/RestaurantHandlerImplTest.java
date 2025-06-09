package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResumeResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerImplTest {

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    @Mock
    private IRestaurantResumeResponseMapper restaurantResumeResponseMapper;

    @InjectMocks
    private RestaurantHandlerImpl restaurantHandler;

    @Test
    void testCreateRestaurant() {
        RestaurantRequestDto requestDto = new RestaurantRequestDto("My Restaurant", "231321321", "Address", "+545231321321", "logo.png", 1L);

        Restaurant domainRestaurant = Restaurant.builder()
                .name("My Restaurant")
                .nit("231321321")
                .urlLogo("logo.png")
                .address("Address")
                .phoneNumber("+545231321321")
                .idOwner(1L)
                .build();

        Restaurant createdRestaurant = Restaurant.builder()
                .id(10L)
                .name("My Restaurant")
                .nit("231321321")
                .urlLogo("logo.png")
                .address("Address")
                .phoneNumber("+545231321321")
                .idOwner(1L)
                .build();

        RestaurantResponseDto responseDto = RestaurantResponseDto.builder()
                                                .id(1L)
                                                .name("My Restaurant")
                                                .nit("231321321")
                                                .address("Address")
                                                .phoneNumber("+545231321321")
                                                .urlLogo("logo.png")
                                                .idOwner(1L)
                                                .build();

        when(restaurantRequestMapper.toRestaurant(requestDto)).thenReturn(domainRestaurant);
        when(restaurantServicePort.createRestaurant(domainRestaurant)).thenReturn(createdRestaurant);
        when(restaurantResponseMapper.toRestaurantResponseDto(createdRestaurant)).thenReturn(responseDto);

        RestaurantResponseDto result = restaurantHandler.createRestaurant(requestDto);

        assertEquals(responseDto, result);
        verify(restaurantRequestMapper).toRestaurant(requestDto);
        verify(restaurantServicePort).createRestaurant(domainRestaurant);
        verify(restaurantResponseMapper).toRestaurantResponseDto(createdRestaurant);
    }

    @Test
    void testRestaurantList() {
        int pageIndex = 0;
        int elementsPerPage = 2;

        List<Restaurant> restaurantList = List.of(

                Restaurant.builder()
                        .id(1L)
                        .name("Rest1")
                        .urlLogo("logo1.png")
                        .nit("NIT1")
                        .address("Address1")
                        .idOwner(1L)
                        .build(),
                Restaurant.builder()
                        .id(2L)
                        .name("Rest2")
                        .urlLogo("logo2.png")
                        .nit("NIT2")
                        .address("Address2")
                        .idOwner(2L)
                        .build()
        );

        List<RestaurantResumeResponseDto> resumeDtos = List.of(
                new RestaurantResumeResponseDto("Rest1", "logo1.png"),
                new RestaurantResumeResponseDto("Rest2", "logo2.png")
        );

        when(restaurantServicePort.findAllRestaurants(pageIndex, elementsPerPage)).thenReturn(restaurantList);
        when(restaurantResumeResponseMapper.toResumeDtoList(restaurantList)).thenReturn(resumeDtos);

        List<RestaurantResumeResponseDto> result = restaurantHandler.restaurantList(pageIndex, elementsPerPage);

        assertEquals(2, result.size());
        assertEquals("Rest1", result.get(0).name());
        assertEquals("Rest2", result.get(1).name());

        verify(restaurantServicePort).findAllRestaurants(pageIndex, elementsPerPage);
        verify(restaurantResumeResponseMapper).toResumeDtoList(restaurantList);
    }
}