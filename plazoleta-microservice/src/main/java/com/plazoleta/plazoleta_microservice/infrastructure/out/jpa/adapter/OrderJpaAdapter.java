package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.spi.IOrderPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderDishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderDishId;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderDishRepository;
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
    private final IDishRepository dishRepository;
    private final IOrderDishRepository orderDishRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public Order saveOrder(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        List<OrderDishEntity> dishEntities = order.getDishes().stream()
                .map(orderDish -> {
                    OrderDishEntity dishEntity = new OrderDishEntity();

                    DishEntity dish = dishRepository.findById(orderDish.getDishId())
                            .orElseThrow(() -> new RuntimeException("Dish not found"));

                    dishEntity.setOrder(savedOrder);
                    dishEntity.setDish(dish);
                    dishEntity.setQuantity(orderDish.getQuantity());
                    dishEntity.setId(new OrderDishId(savedOrder.getId(), dish.getId()));

                    return dishEntity;
                })
                .toList();


        orderDishRepository.saveAll(dishEntities);
        return orderEntityMapper.toDomain(savedOrder);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderEntityMapper::toDomain);
    }

    @Override
    public List<Order> findOrdersByCustomerId(Long customerId) {
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
    public List<Order> findOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage) {
        Page<OrderEntity> orderPage = orderRepository.findAllByRestaurantIdAndStatus(restaurantId, status, PageRequest.of(pageIndex, elementsPerPage, Sort.by(Sort.Direction.ASC, "orderDate")));

        return orderPage.getContent().stream()
                .map(orderEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Order updateOrder(Order order) {
        OrderEntity entity = orderEntityMapper.toEntity(order);
        return orderEntityMapper.toDomain(orderRepository.save(entity));
    }
}
