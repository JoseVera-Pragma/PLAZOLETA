package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestauranteResumenResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantHandlerImplTest {
    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    @Mock
    private IRestauranteResumenResponseMapper restauranteResumenResponseMapper;

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

    @Test
    void shouldReturnPaginatedListOfRestaurantDto(){
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Restaurant r1 = new Restaurant.Builder()
                .id(1L)
                .name("A")
                .urlLogo("Logo 1")
                .nit("1321231321")
                .phoneNumber("31215454")
                .address("calle 1 2d2d")
                .idOwner(1L)
                .build();

        Restaurant r2 = new Restaurant.Builder()
                .id(2L)
                .name("B")
                .urlLogo("Logo 2")
                .nit("1321231321")
                .phoneNumber("31215454")
                .address("calle 1 2d2d")
                .idOwner(1L)
                .build();

        List<Restaurant> restaurantList = List.of(r1, r2);

        Page<Restaurant> restaurantPage = new PageImpl<>(restaurantList, pageable, restaurantList.size());

        RestaurantResumeResponseDto dto1 =  new RestaurantResumeResponseDto("A", "Logo 1");
        RestaurantResumeResponseDto dto2 =  new RestaurantResumeResponseDto("B", "Logo 2");
        List<RestaurantResumeResponseDto> dtoList = List.of(dto1,dto2);

        when(restaurantServicePort.findAll(pageable)).thenReturn(restaurantPage);
        when(restauranteResumenResponseMapper.toResumenDtoList(restaurantList)).thenReturn(dtoList);

        Page<RestaurantResumeResponseDto> result = restaurantHandler.restaurantList(page,size);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("A", result.getContent().get(0).name());
        assertEquals("B", result.getContent().get(1).name());

        verify(restaurantServicePort).findAll(pageable);
        verify(restauranteResumenResponseMapper).toResumenDtoList(restaurantList);
    }
}