package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;

import java.util.List;

public interface IOrderPersistencePort {

    void saveOrder(Order order);

    Order getOrderById(Long orderId);

    List<Order> getOrdersByCustomerId(Long customerId);

    boolean customerHasOrdersInProcess(Long customerId);

    List<Order> getOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage);

    void updateOrder(Order order);
}
