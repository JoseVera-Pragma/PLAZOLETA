package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRestaurantHandler restaurantHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRestaurant_ReturnsCreated() throws Exception {
        RestaurantRequestDto dto = RestaurantRequestDto.builder()
                .name("Restaurante A")
                .nit("90012341456")
                .address("Calle 123")
                .phoneNumber("123456789")
                .urlLogo("url")
                .idOwner(1L)
                .build();

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(restaurantHandler, times(1)).createRestaurant(refEq(dto));
    }
}