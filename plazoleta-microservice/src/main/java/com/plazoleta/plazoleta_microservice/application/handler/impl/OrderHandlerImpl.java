package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.OrderRequestMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandlerImpl implements IOrderHandler {
    private final IOrderServicePort orderServicePort;
    private final IAuthenticatedUserHandler authenticatedUserHandler;
    private final OrderRequestMapper orderRequestMapper;

    @Override
    public void createOrder(CreateOrderRequestDto createOrderRequestDto) {

        Long userId = authenticatedUserHandler.getCurrentUserId();

        Order order = orderRequestMapper.toDomain(createOrderRequestDto);

        order.setCustomerId(userId);

        orderServicePort.createOrder(order);

    }
}
