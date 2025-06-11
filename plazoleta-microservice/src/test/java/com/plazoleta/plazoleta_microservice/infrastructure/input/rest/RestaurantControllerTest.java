package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRestaurantHandler restaurantHandler;

    @MockitoBean
    private JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    private RestaurantRequestDto restaurantRequestDto;
    private RestaurantResponseDto restaurantResponseDto;

    @BeforeEach
    void setUp() {
        restaurantRequestDto = RestaurantRequestDto.builder()
                .name("Testaurant")
                .address("Calle 123")
                .phoneNumber("1234567")
                .urlLogo("http://logo.com")
                .nit("123456")
                .idOwner(1L)
                .build();

        restaurantResponseDto = RestaurantResponseDto.builder()
                .id(1L)
                .name("Testaurant")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRestaurant_shouldReturnCreated() throws Exception {
        when(restaurantHandler.createRestaurant(any(RestaurantRequestDto.class)))
                .thenReturn(restaurantResponseDto);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Testaurant"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getRestaurants_shouldReturnList() throws Exception {
        int page = 0;
        int size = 2;

        List<RestaurantResumeResponseDto> dtoList = List.of(
                new RestaurantResumeResponseDto( "Pizza Loca", "url1"),
                new RestaurantResumeResponseDto( "Sushi Zen", "url2")
        );

        Page<RestaurantResumeResponseDto> pageResponse = new Page<>(
                dtoList,
                page,
                size,
                5L
        );

        when(restaurantHandler.restaurantPage(page, size)).thenReturn(pageResponse);

        mockMvc.perform(get("/restaurants")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(2));

        verify(restaurantHandler).restaurantPage(page, size);
    }
}