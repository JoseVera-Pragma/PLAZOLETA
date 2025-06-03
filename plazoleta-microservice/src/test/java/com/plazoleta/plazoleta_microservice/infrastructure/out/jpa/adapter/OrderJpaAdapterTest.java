package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.OrderNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.OrderEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
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
    private IRestaurantRepository restaurantRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @InjectMocks
    private OrderJpaAdapter orderJpaAdapter;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrder_shouldSaveOrderCorrectly() {
        Order order = new Order(1L, 10L, null, 100L, LocalDateTime.now(), OrderStatus.PENDING,
                List.of(new OrderDish(1L, 2)));

        RestaurantEntity restaurant = new RestaurantEntity();
        DishEntity dishEntity = new DishEntity();
        List<DishEntity> dishes = List.of(dishEntity);

        OrderEntity mappedEntity = new OrderEntity();

        when(restaurantRepository.findById(100L)).thenReturn(Optional.of(restaurant));
        when(dishRepository.findAllById(List.of(1L))).thenReturn(dishes);
        when(orderEntityMapper.toEntity(order, restaurant, dishes)).thenReturn(mappedEntity);

        orderJpaAdapter.saveOrder(order);

        verify(orderRepository).save(mappedEntity);
    }

    @Test
    void saveOrder_shouldThrowIfRestaurantNotFound() {
        Order order = new Order(null, 1L, null, 99L, null, OrderStatus.PENDING, List.of());

        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            orderJpaAdapter.saveOrder(order);
        });

        assertTrue(ex.getMessage().contains("Restaurant not found"));
    }

    @Test
    void getOrderById_shouldReturnMappedOrder() {
        OrderEntity entity = new OrderEntity();
        Order expectedOrder = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderEntityMapper.toDomain(entity)).thenReturn(expectedOrder);

        Order result = orderJpaAdapter.getOrderById(1L);

        assertEquals(expectedOrder, result);
    }

    @Test
    void getOrderById_shouldThrowIfNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            orderJpaAdapter.getOrderById(999L);
        });
    }

    @Test
    void getOrdersByCustomerId_shouldMapOrdersCorrectly() {
        OrderEntity entity1 = new OrderEntity();
        OrderEntity entity2 = new OrderEntity();

        Order domain1 = new Order();
        Order domain2 = new Order();

        when(orderRepository.findByCustomerId(100L)).thenReturn(List.of(entity1, entity2));
        when(orderEntityMapper.toDomain(entity1)).thenReturn(domain1);
        when(orderEntityMapper.toDomain(entity2)).thenReturn(domain2);

        List<Order> result = orderJpaAdapter.getOrdersByCustomerId(100L);

        assertEquals(2, result.size());
        assertTrue(result.contains(domain1));
        assertTrue(result.contains(domain2));
    }

    @Test
    void customerHasOrdersInProcess_shouldReturnTrueIfExists() {
        when(orderRepository.existsByCustomerIdAndStatusIn(
                eq(10L),
                anyList()
        )).thenReturn(true);

        boolean result = orderJpaAdapter.customerHasOrdersInProcess(10L);
        assertTrue(result);
    }

    @Test
    void customerHasOrdersInProcess_shouldReturnFalseIfNotExists() {
        when(orderRepository.existsByCustomerIdAndStatusIn(
                eq(20L),
                anyList()
        )).thenReturn(false);

        boolean result = orderJpaAdapter.customerHasOrdersInProcess(20L);
        assertFalse(result);
    }

    @Test
    void getOrdersByStatusAndRestaurantId_ShouldReturnMappedOrders() {
        Long restaurantId = 1L;
        OrderStatus status = OrderStatus.PENDING;
        int pageIndex = 0;
        int elementsPerPage = 2;

        OrderEntity entity1 = new OrderEntity();
        OrderEntity entity2 = new OrderEntity();
        List<OrderEntity> entityList = List.of(entity1, entity2);

        Page<OrderEntity> orderPage = new PageImpl<>(entityList);

        Order order1 = new Order();
        Order order2 = new Order();

        when(orderRepository.findAllByRestaurantIdAndStatus(
                eq(restaurantId),
                eq(status),
                any(PageRequest.class))
        ).thenReturn(orderPage);

        when(orderEntityMapper.toDomain(entity1)).thenReturn(order1);
        when(orderEntityMapper.toDomain(entity2)).thenReturn(order2);

        List<Order> result = orderJpaAdapter.getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(order1, result.get(0));
        assertEquals(order2, result.get(1));

        verify(orderRepository).findAllByRestaurantIdAndStatus(eq(restaurantId), eq(status), any(PageRequest.class));
        verify(orderEntityMapper).toDomain(entity1);
        verify(orderEntityMapper).toDomain(entity2);
    }

    @Test
    void updateOrder_ShouldUpdateChefIdAndStatus() {
        Long orderId = 1L;
        Long chefId = 10L;
        OrderStatus status = OrderStatus.IN_PREPARATION;

        Order order = new Order();
        order.setId(orderId);
        order.setChefId(chefId);
        order.setStatus(status);

        OrderEntity existingEntity = new OrderEntity();
        existingEntity.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingEntity));

        orderJpaAdapter.updateOrder(order);

        assertEquals(chefId, existingEntity.getChefId());
        assertEquals(status, existingEntity.getStatus());
        verify(orderRepository).save(existingEntity);
    }

    @Test
    void updateOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderJpaAdapter.updateOrder(order));
        verify(orderRepository, never()).save(any());
    }
}