package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.spi.IOrderPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.OrderNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;


    @Override
    public void saveOrder(Order order) {
        OrderEntity entity = orderEntityMapper.toOrderEntity(order);

        orderRepository.save(entity);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderEntityMapper::toOrder);
    }

    @Override
    public List<Order> findOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderEntityMapper::toOrder)
                .toList();
    }

    @Override
    public boolean customerHasOrdersInProcess(Long customerId) {
        List<OrderStatus> orderInProcess = new ArrayList<>(
                List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY)
        );

        return orderRepository.existsByCustomerIdAndStatusIn(customerId, orderInProcess);

    }

    @Override
    public List<Order> findOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage) {
        Page<OrderEntity> orderPage = orderRepository.findAllByRestaurantIdAndStatus(restaurantId, status, PageRequest.of(pageIndex, elementsPerPage, Sort.by(Sort.Direction.ASC, "orderDate")));

        return orderPage.getContent().stream()
                .map(orderEntityMapper::toOrder)
                .toList();
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.save(orderEntityMapper.toOrderEntity(order));
    }
}
