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

        List<OrderResponseDto> responseDtos = List.of(
                new OrderResponseDto(1L, 100L, 200L, "2024-01-01", status, "Pending", "1234", 10L),
                new OrderResponseDto(2L, 101L, 201L, "2024-01-02", status, "Pending", "1234",10L)
        );

        when(orderServicePort.findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage)).thenReturn(orders);
        when(orderResponseMapper.toResponsesDto(orders)).thenReturn(responseDtos);

        List<OrderResponseDto> result = orderHandler.findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage);

        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(orderServicePort).findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage);
        verify(orderResponseMapper).toResponsesDto(orders);
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