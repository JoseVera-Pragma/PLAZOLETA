package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

import java.util.List;

public interface IOrderServicePort {

    void createOrder(Order order);

    List<Order> getOrdersByCustomerId(Long customerId);

    Order findOrderById(Long orderId);

    Page<Order> findOrdersByStatusForAuthenticatedEmployee(OrderStatus status, int pageIndex, int elementsPerPage);

    void assignOrder(Long orderId);

    void markOrderAsReady(Long orderId);

    void markOrderAsDelivered(Long orderId, String securityPin);

    void markOrderAsCanceled(Long orderId);
}
