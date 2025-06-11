package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DeliverOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.IOrderRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IOrderResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderHandlerImplTest {

    @Mock
    private IOrderServicePort orderServicePort;

    @Mock
    private IOrderRequestMapper orderRequestMapper;

    @Mock
    private IOrderResponseMapper orderResponseMapper;

    @InjectMocks
    private OrderHandlerImpl orderHandler;

    @Test
    void testCreateOrder() {
        DishOrderRequestDto dish1 = new DishOrderRequestDto(1L, 2);
        ArrayList<DishOrderRequestDto> dishes = new ArrayList<>();
        dishes.add(dish1);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setIdRestaurant(10L);
        requestDto.setDishes(dishes);


        Restaurant restaurant = Restaurant.builder().id(10L).build();

        Order order = Order.builder()
                .restaurant(restaurant)
                .build();

        when(orderRequestMapper.toOrder(requestDto)).thenReturn(order);

        orderHandler.createOrder(requestDto);

        verify(orderRequestMapper).toOrder(requestDto);
        verify(orderServicePort).createOrder(order);
    }

    @Test
    void testFindOrdersByStatusForAuthenticatedEmployee() {
        OrderStatus status = OrderStatus.PENDING;
        int pageIndex = 0;
        int elementsPerPage = 5;

        List<Order> orders = List.of(
                Order.builder().id(1L).build(),
                Order.builder().id(2L).build()
        );

        Page<Order> orderPage = new Page<>(orders, pageIndex, elementsPerPage, orders.size());

        List<OrderResponseDto> responseDtos = List.of(
                new OrderResponseDto(1L, 100L, 200L, "2024-01-01", status, "Pendiente", "1234", 10L),
                new OrderResponseDto(2L, 101L, 201L, "2024-01-02", status, "Pendiente", "1234", 10L)
        );

        Page<OrderResponseDto> expectedResponsePage = new Page<>(responseDtos, pageIndex, elementsPerPage, responseDtos.size());

        when(orderServicePort.findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage)).thenReturn(orderPage);
        when(orderResponseMapper.toOrderResponsePage(orderPage)).thenReturn(expectedResponsePage);

        Page<OrderResponseDto> result = orderHandler.findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().getFirst().getId());

        verify(orderServicePort).findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage);
        verify(orderResponseMapper).toOrderResponsePage(orderPage);
    }

    @Test
    void testAssignOrder() {
        Long orderId = 99L;

        orderHandler.assignOrder(orderId);

        verify(orderServicePort).assignOrder(orderId);
    }

    @Test
    void testMarkOrderAsReady() {
        Long orderId = 99L;

        orderHandler.markOrderAsReady(orderId);

        verify(orderServicePort).markOrderAsReady(orderId);
    }

    @Test
    void testMarkOrderAsDelivered() {
        Long orderId = 1L;
        String pin = "1234";
        DeliverOrderRequestDto dto = new DeliverOrderRequestDto(pin);

        orderHandler.markOrderAsDelivered(orderId, dto);

        verify(orderServicePort).markOrderAsDelivered(orderId, pin);
    }

    @Test
    void testMarkOrderAsCanceled() {
        Long orderId = 1L;

        orderHandler.markOrderAsCanceled(orderId);

        verify(orderServicePort).markOrderAsCanceled(orderId);
    }
}