package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOrderHandler orderHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void createOrder_shouldReturnCreated() throws Exception {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setIdRestaurant(1L);
        requestDto.setDishes(new ArrayList<>(List.of(
                new DishOrderRequestDto(1L, 2),
                new DishOrderRequestDto(2L, 3)
        )));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"EMPLOYED"})
    void getOrdersByStatus_ShouldReturnListOfOrders() throws Exception {
        Long restaurantId = 1L;
        OrderStatus status = OrderStatus.PENDING;
        int page = 0;
        int pageSize = 10;

        List<OrderResponseDto> mockResponse = List.of(
                new OrderResponseDto(1L, 1L,null,"2025-06-02T16:14:15.119426", OrderStatus.PENDING, "Pendiente",1L),
                new OrderResponseDto(2L, 1L,null, "2025-06-02T16:14:15.119426", OrderStatus.PENDING, "Pendiente",1L)
        );

        when(orderHandler.getOrdersByStatusAndRestaurantId(restaurantId, status, page, pageSize)).thenReturn(mockResponse);

        mockMvc.perform(get("/orders/getOrdersByStatus/{restaurantId}/{status}/{page}/{pageSize}",
                        restaurantId, status, page, pageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].statusDescription").value("Pendiente"));
    }
}