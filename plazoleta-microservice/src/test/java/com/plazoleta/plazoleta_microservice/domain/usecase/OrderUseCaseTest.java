package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.*;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;
    @Mock
    private IDishPersistencePort dishPersistencePort;
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;
    @Mock
    private IUserServiceClientPort userServiceClientPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldSaveOrder_whenValid() {
        Long customerId = 1L;
        Long restaurantId = 10L;
        Dish dish = Dish.builder().id(100L).restaurantId(restaurantId).build();
        OrderDish orderDish = new OrderDish(100L, 1);
        Order order = Order.builder()
                .restaurantId(restaurantId)
                .dishes(List.of(orderDish))
                .build();

        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(Restaurant.builder().id(restaurantId).build()));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(dishPersistencePort.findDishById(100L)).thenReturn(Optional.of(dish));
        doNothing().when(orderPersistencePort).saveOrder(any(Order.class));

        orderUseCase.createOrder(order);

        verify(orderPersistencePort).saveOrder(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowIllegalStateException_whenCustomerHasOrdersInProcess() {
        Long customerId = 1L;
        Order order = Order.builder().customerId(customerId).build();

        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderUseCase.createOrder(order));
        assertEquals("Customer have order in process.", ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long restaurantId = 10L;
        OrderDish orderDish = new OrderDish(100L, 1);
        Order order = Order.builder()
                .restaurantId(restaurantId)
                .dishes(List.of(orderDish))
                .build();

        when(orderPersistencePort.customerHasOrdersInProcess(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(Restaurant.builder().id(restaurantId).build()));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderUseCase.createOrder(order));
    }

    @Test
    void createOrder_shouldThrowDishNotFoundException_whenDishNotFound() {
        Long customerId = 1L;
        Long restaurantId = 10L;
        OrderDish orderDish = new OrderDish(100L, 1);
        Order order = Order.builder()
                .restaurantId(restaurantId)
                .dishes(List.of(orderDish))
                .build();

        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(Restaurant.builder().id(restaurantId).build()));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(dishPersistencePort.findDishById(100L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> orderUseCase.createOrder(order));
    }

    @Test
    void createOrder_shouldThrowIllegalArgumentException_whenDishFromDifferentRestaurant() {
        Long customerId = 1L;
        Long restaurantId = 10L;
        Long differentRestaurantId = 20L;

        Dish dish = Dish.builder().id(100L).restaurantId(differentRestaurantId).build();
        OrderDish orderDish = new OrderDish(100L, 1);
        Order order = Order.builder()
                .restaurantId(restaurantId)
                .dishes(List.of(orderDish))
                .build();

        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(Restaurant.builder().id(restaurantId).build()));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(dishPersistencePort.findDishById(100L)).thenReturn(Optional.of(dish));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> orderUseCase.createOrder(order));
        assertEquals("All dishes must belong to the same restaurant.", ex.getMessage());
    }

    @Test
    void getOrdersByCustomerId_shouldReturnOrders() {
        Long customerId = 1L;
        List<Order> orders = List.of(Order.builder().id(1L).build());
        when(orderPersistencePort.findOrdersByCustomerId(customerId)).thenReturn(orders);

        List<Order> result = orderUseCase.getOrdersByCustomerId(customerId);

        assertEquals(orders, result);
    }

    @Test
    void findOrderById_shouldReturnOrder_whenExists() {
        Long orderId = 5L;
        Order order = Order.builder().id(orderId).build();
        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));

        Order result = orderUseCase.findOrderById(orderId);

        assertEquals(order, result);
    }

    @Test
    void findOrderById_shouldThrowOrderNotFoundException_whenNotFound() {
        Long orderId = 5L;
        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderUseCase.findOrderById(orderId));
    }

    @Test
    void findOrdersByStatusForAuthenticatedEmployee_shouldReturnOrders() {
        Long employedId = 1L;
        Long restaurantId = 10L;
        OrderStatus status = OrderStatus.PENDING;
        List<Order> orders = List.of(Order.builder().id(1L).build());

        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));
        when(userServiceClientPort.findUserById(employedId)).thenReturn(
                com.plazoleta.plazoleta_microservice.domain.model.User.builder()
                        .restaurantId(restaurantId)
                        .build());
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(Restaurant.builder().id(restaurantId).build()));
        when(orderPersistencePort.findOrdersByStatusAndRestaurantId(restaurantId, status, 0, 10)).thenReturn(orders);

        List<Order> result = orderUseCase.findOrdersByStatusForAuthenticatedEmployee(status, 0, 10);

        assertEquals(orders, result);
    }

    @Test
    void findOrdersByStatusForAuthenticatedEmployee_shouldThrowUserNotFoundException_whenUserNotFound() {
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> orderUseCase.findOrdersByStatusForAuthenticatedEmployee(OrderStatus.PENDING, 0, 10));
    }

    @Test
    void findOrdersByStatusForAuthenticatedEmployee_shouldThrowRestaurantNotFoundException_whenRestaurantNotFound() {
        Long employedId = 1L;
        Long restaurantId = 10L;

        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));
        when(userServiceClientPort.findUserById(employedId)).thenReturn(
                com.plazoleta.plazoleta_microservice.domain.model.User.builder()
                        .restaurantId(restaurantId)
                        .build());
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
                () -> orderUseCase.findOrdersByStatusForAuthenticatedEmployee(OrderStatus.PENDING, 0, 10));
    }

    @Test
    void assignOrder_shouldUpdateOrder_whenValid() {
        Long orderId = 5L;
        Long employedId = 1L;
        Order order = Order.builder().id(orderId).build();

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));

        orderUseCase.assignOrder(orderId);

        verify(orderPersistencePort).updateOrder(argThat(updatedOrder ->
                updatedOrder.getChefId().equals(employedId) &&
                        updatedOrder.getStatus() == OrderStatus.IN_PREPARATION
        ));
    }

    @Test
    void assignOrder_shouldThrowOrderNotFoundException_whenOrderNotFound() {
        Long orderId = 5L;
        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderUseCase.assignOrder(orderId));
    }

    @Test
    void assignOrder_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long orderId = 5L;
        Order order = Order.builder().id(orderId).build();

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderUseCase.assignOrder(orderId));
    }
}