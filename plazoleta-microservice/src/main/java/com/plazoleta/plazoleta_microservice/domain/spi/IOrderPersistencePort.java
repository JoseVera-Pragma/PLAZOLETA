package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface IOrderPersistencePort {

    void saveOrder(Order order);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrdersByCustomerId(Long customerId);

    boolean customerHasOrdersInProcess(Long customerId);

    List<Order> findOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage);

    void updateOrder(Order order);
}
