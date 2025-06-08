package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DeliverOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOrderHandler orderHandler;

    @MockitoBean
    private JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderRequestDto createOrderRequestDto;
    private OrderResponseDto orderResponseDto;

    @BeforeEach
    void setUp() {
        createOrderRequestDto = new CreateOrderRequestDto(
                1L,
                new ArrayList<>(
                        List.of(
                                new DishOrderRequestDto(1L, 2),
                                new DishOrderRequestDto(2L, 2)
                        )
                )
        );

        orderResponseDto = new OrderResponseDto(1L, 1L, 1L, "2025", OrderStatus.PENDING, "Pendiente", "1234",1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createOrder_shouldReturnCreated() throws Exception {
        doNothing().when(orderHandler).createOrder(any(CreateOrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "EMPLOYED")
    void getOrdersByStatus_shouldReturnOrderList() throws Exception {
        when(orderHandler.findOrdersByStatusForAuthenticatedEmployee(OrderStatus.PENDING, 0, 10))
                .thenReturn(List.of(orderResponseDto));

        mockMvc.perform(get("/orders/status/PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYED")
    void assignOrderToEmployed_shouldReturnNoContent() throws Exception {
        doNothing().when(orderHandler).assignOrder(1L);

        mockMvc.perform(patch("/orders/assign/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void markOrderAsReady_ShouldReturnOk() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(patch("/orders/{orderId}/ready", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderHandler).markOrderAsReady(orderId);
    }

    @Test
    @WithMockUser(roles = "EMPLOYED")
    void markOrderAsDelivered_ShouldReturnOk() throws Exception {
        Long orderId = 1L;
        DeliverOrderRequestDto requestDto = new DeliverOrderRequestDto("1234");

        mockMvc.perform(patch("/orders/{orderId}/delivered", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(orderHandler).markOrderAsDelivered(orderId, requestDto);
    }
}