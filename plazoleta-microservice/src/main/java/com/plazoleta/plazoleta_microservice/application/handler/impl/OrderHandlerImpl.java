package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DeliverOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IOrderRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IOrderResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandlerImpl implements IOrderHandler {
    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public void createOrder(CreateOrderRequestDto createOrderRequestDto) {
        Order order = orderRequestMapper.toOrder(createOrderRequestDto);
        orderServicePort.createOrder(order);
    }

    @Override
    public Page<OrderResponseDto> findOrdersByStatusForAuthenticatedEmployee(OrderStatus status, int pageIndex, int elementsPerPage) {
        return orderResponseMapper.toOrderResponsePage(orderServicePort.findOrdersByStatusForAuthenticatedEmployee(status, pageIndex, elementsPerPage));
    }

    @Override
    public void assignOrder(Long orderId) {
        orderServicePort.assignOrder(orderId);
    }

    @Override
    public void markOrderAsReady(Long orderId) {
        orderServicePort.markOrderAsReady(orderId);
    }

    @Override
    public void markOrderAsDelivered(Long orderId, DeliverOrderRequestDto requestDto) {
        orderServicePort.markOrderAsDelivered(orderId, requestDto.getPin());
    }

    @Override
    public void markOrderAsCanceled(Long orderId){
        orderServicePort.markOrderAsCanceled(orderId);
    }
}
