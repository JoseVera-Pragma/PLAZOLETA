package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderDishRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderJpaAdapterTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private IDishRepository dishRepository;

    @Mock
    private IOrderDishRepository orderDishRepository;

    @Mock
    private IOrderEntityMapper orderEntityMapper;

    @InjectMocks
    private OrderJpaAdapter orderJpaAdapter;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Restaurant restaurant = Restaurant.builder().id(10L).build();

        order = Order.builder()
                .id(1L)
                .customerId(100L)
                .restaurant(restaurant)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .dishes(List.of(new OrderDish(5L,2)))
                .build();

        orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setCustomerId(100L);
        orderEntity.setRestaurant(new RestaurantEntity());
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setOrderDate(LocalDateTime.now());
    }

    @Test
    void testSaveOrder() {
        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(5L);

        OrderEntity savedOrderEntity = new OrderEntity();
        savedOrderEntity.setId(1L);

        when(orderEntityMapper.toEntity(order)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(savedOrderEntity);
        when(dishRepository.findById(5L)).thenReturn(Optional.of(dishEntity));

        orderJpaAdapter.saveOrder(order);

        verify(orderRepository).save(orderEntity);
        verify(dishRepository).findById(5L);
        verify(orderDishRepository).saveAll(anyList());
    }

    @Test
    void testFindOrderByIdFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        Optional<Order> result = orderJpaAdapter.findOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    void testFindOrderByIdNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Order> result = orderJpaAdapter.findOrderById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindOrdersByCustomerId() {
        List<OrderEntity> entities = List.of(orderEntity);

        when(orderRepository.findByCustomerId(100L)).thenReturn(entities);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        List<Order> result = orderJpaAdapter.findOrdersByCustomerId(100L);

        assertEquals(1, result.size());
        assertEquals(order, result.getFirst());
    }

    @Test
    void testCustomerHasOrdersInProcessTrue() {
        when(orderRepository.existsByCustomerIdAndStatusIn(eq(100L), anyList())).thenReturn(true);

        assertTrue(orderJpaAdapter.customerHasOrdersInProcess(100L));
    }

    @Test
    void testCustomerHasOrdersInProcessFalse() {
        when(orderRepository.existsByCustomerIdAndStatusIn(eq(100L), anyList())).thenReturn(false);

        assertFalse(orderJpaAdapter.customerHasOrdersInProcess(100L));
    }

    @Test
    void testFindOrdersByStatusAndRestaurantId() {
        Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity));
        when(orderRepository.findAllByRestaurantIdAndStatus(eq(10L), eq(OrderStatus.PENDING), any(PageRequest.class))).thenReturn(page);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        List<Order> result = orderJpaAdapter.findOrdersByStatusAndRestaurantId(10L, OrderStatus.PENDING, 0, 10);

        assertEquals(1, result.size());
        assertEquals(order, result.getFirst());
    }

    @Test
    void testUpdateOrder() {
        when(orderEntityMapper.toEntity(order)).thenReturn(orderEntity);

        orderJpaAdapter.updateOrder(order);

        verify(orderRepository).save(orderEntity);
    }
}