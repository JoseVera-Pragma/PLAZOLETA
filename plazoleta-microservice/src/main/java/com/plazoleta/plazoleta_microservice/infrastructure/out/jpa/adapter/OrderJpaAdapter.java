package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.spi.IOrderPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.OrderNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.OrderEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IDishRepository dishRepository;
    private final IRestaurantRepository restaurantRepository;
    private final OrderEntityMapper orderEntityMapper;


    @Override
    public void saveOrder(Order order) {

        RestaurantEntity restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found whit ID: " + order.getRestaurantId()));

        List<Long> dishIds = order.getDishes().stream()
                .map(OrderDish::getDishId)
                .toList();

        List<DishEntity> dishEntities = dishRepository.findAllById(dishIds);

        OrderEntity entity = orderEntityMapper.toEntity(order, restaurant, dishEntities);

        orderRepository.save(entity);
    }

    @Override
    public Order getOrderById(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return orderEntityMapper.toDomain(orderEntity);
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderEntityMapper::toDomain)
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
    public List<Order> getOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage) {
        Page<OrderEntity> orderPage = orderRepository.findAllByRestaurantIdAndStatus(restaurantId, status, PageRequest.of(pageIndex, elementsPerPage, Sort.by(Sort.Direction.ASC, "orderDate")));

        return orderPage.getContent().stream()
                .map(orderEntityMapper::toDomain)
                .toList();
    }
}
