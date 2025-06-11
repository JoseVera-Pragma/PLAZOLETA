package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import org.aspectj.weaver.ast.Or;

import java.util.List;
import java.util.Optional;

public interface IOrderPersistencePort {

    Order saveOrder(Order order);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrdersByCustomerId(Long customerId);

    boolean customerHasOrdersInProcess(Long customerId);

    Page<Order> findOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage);

    Order updateOrder(Order order);
}
