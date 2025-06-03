package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Order;

import java.util.List;

public interface IOrderServicePort {

    void createOrder(Order order);

    List<Order> getOrdersByClientId(Long clientId);

    Order getOrderById(Long orderId);


}
