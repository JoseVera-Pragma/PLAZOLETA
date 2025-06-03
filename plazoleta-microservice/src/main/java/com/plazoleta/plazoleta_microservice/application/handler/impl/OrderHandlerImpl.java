package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IOrderResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.OrderRequestMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandlerImpl implements IOrderHandler {
    private final IOrderServicePort orderServicePort;
    private final IAuthenticatedUserHandler authenticatedUserHandler;
    private final OrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public void createOrder(CreateOrderRequestDto createOrderRequestDto) {

        Long userId = authenticatedUserHandler.getCurrentUserId();

        Order order = orderRequestMapper.toDomain(createOrderRequestDto);

        order.setCustomerId(userId);

        orderServicePort.createOrder(order);

    }

    @Override
    public List<OrderResponseDto> getOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage) {
        return orderResponseMapper.toResponsesDto(orderServicePort.getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage));
    }

    @Override
    public void assignOrder(Long orderId) {
        Long employedId = authenticatedUserHandler.getCurrentUserId();
        orderServicePort.assignOrder(orderId, employedId);
    }
}
