package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IOrderEntityMapper;
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
    private IOrderEntityMapper orderEntityMapper;

    @InjectMocks
    private OrderJpaAdapter orderJpaAdapter;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = Order.builder()
                .id(1L)
                .customerId(100L)
                .restaurantId(10L)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .build();

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setCustomerId(100L);
        orderEntity.setRestaurant(restaurantEntity);
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setOrderDate(LocalDateTime.now());
    }

    @Test
    void testSaveOrder() {
        when(orderEntityMapper.toOrderEntity(order)).thenReturn(orderEntity);

        orderJpaAdapter.saveOrder(order);

        verify(orderRepository).save(orderEntity);
    }

    @Test
    void testFindOrderByIdFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toOrder(orderEntity)).thenReturn(order);

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
        when(orderEntityMapper.toOrder(orderEntity)).thenReturn(order);

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
        when(orderEntityMapper.toOrder(orderEntity)).thenReturn(order);

        List<Order> result = orderJpaAdapter.findOrdersByStatusAndRestaurantId(10L, OrderStatus.PENDING, 0, 10);

        assertEquals(1, result.size());
        assertEquals(order, result.getFirst());
    }

    @Test
    void testUpdateOrder() {
        when(orderEntityMapper.toOrderEntity(order)).thenReturn(orderEntity);

        orderJpaAdapter.updateOrder(order);

        verify(orderRepository).save(orderEntity);
    }
}