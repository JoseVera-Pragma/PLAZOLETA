package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Order;

import java.util.List;

public interface IOrderPersistencePort {

    void saveOrder(Order order);

    Order getOrderById(Long orderId);

    List<Order> getOrdersByCustomerId(Long customerId);

    boolean customerHasOrdersInProcess(Long customerId);
}
