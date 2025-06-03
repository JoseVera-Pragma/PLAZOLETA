package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;

public interface IOrderHandler {
    void createOrder(CreateOrderRequestDto createOrderRequestDto);
}
