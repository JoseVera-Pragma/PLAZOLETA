package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;

import java.util.List;

public interface IOrderServicePort {

    void createOrder(Order order);

    List<Order> getOrdersByCustomerId(Long customerId);

    Order findOrderById(Long orderId);

    List<Order> findOrdersByStatusForAuthenticatedEmployee(OrderStatus status, int pageIndex, int elementsPerPage);

    void assignOrder(Long orderId);
}
