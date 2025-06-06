package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
        RestaurantResumeResponseDto resume = new RestaurantResumeResponseDto("Testaurant", "http://logo.com");

        when(restaurantHandler.restaurantList(0, 10)).thenReturn(List.of(resume));

        mockMvc.perform(get("/restaurants?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Testaurant"));
    }
}