package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.OrderRequestMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}