package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DeliverOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;

import java.util.List;

public interface IOrderHandler {
    void createOrder(CreateOrderRequestDto createOrderRequestDto);

    List<OrderResponseDto> findOrdersByStatusForAuthenticatedEmployee(OrderStatus status, int pageIndex, int elementsPerPage);

    void assignOrder(Long orderId);

    void markOrderAsReady(Long orderId);

    void markOrderAsDelivered(Long orderId, DeliverOrderRequestDto requestDto);

    void markOrderAsCanceled(Long orderId);
}
