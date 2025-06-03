package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IOrderResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.OrderRequestMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderHandlerImplTest {
    @Mock
    private IOrderServicePort orderServicePort;

    @Mock
    private IAuthenticatedUserHandler authenticatedUserHandler;

    @Mock
    private OrderRequestMapper orderRequestMapper;

    @Mock
    private IOrderResponseMapper orderResponseMapper;

    @InjectMocks
    private OrderHandlerImpl orderHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldSetCustomerIdAndCallService() {
        Long userId = 123L;

        CreateOrderRequestDto dto = new CreateOrderRequestDto();
        Order mappedOrder = new Order(1L, null, null, 100L, null, null, List.of());

        when(authenticatedUserHandler.getCurrentUserId()).thenReturn(userId);
        when(orderRequestMapper.toDomain(dto)).thenReturn(mappedOrder);

        orderHandler.createOrder(dto);

        assertEquals(userId, mappedOrder.getCustomerId());
        verify(orderServicePort).createOrder(mappedOrder);
    }

    @Test
    void getOrdersByStatusAndRestaurantId_ShouldReturnDtoList() {
        Long restaurantId = 1L;
        OrderStatus status = OrderStatus.PENDING;
        int pageIndex = 0;
        int elementsPerPage = 5;

        List<Order> orders = List.of(new Order(), new Order());
        List<OrderResponseDto> expectedDtos = List.of(
                new OrderResponseDto(1L,1L,2L, "2025-06-02T16:14:15.119426",OrderStatus.PENDING,"PENDING",1L),
                new OrderResponseDto(2L, 1L,2L, "2025-06-02T16:14:15.119426",OrderStatus.PENDING,"PENDING",1L)
        );

        when(orderServicePort.getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage))
                .thenReturn(orders);

        when(orderResponseMapper.toResponsesDto(orders))
                .thenReturn(expectedDtos);

        List<OrderResponseDto> result = orderHandler.getOrdersByStatusAndRestaurantId(
                restaurantId, status, pageIndex, elementsPerPage
        );

        assertEquals(expectedDtos, result);
        verify(orderServicePort).getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage);
        verify(orderResponseMapper).toResponsesDto(orders);
    }
}