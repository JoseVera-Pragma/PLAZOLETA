package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.InvalidSecurityPinException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.InvalidOrderStatusException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.*;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    @Mock
    private ISendSmsPort sendSmsPort;

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
        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();

        Order order = Order.builder()
                .restaurant(restaurant)
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

        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderUseCase.createOrder(order));
        assertEquals("Customer have order in process.", ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long restaurantId = 10L;
        OrderDish orderDish = new OrderDish(100L, 1);
        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
        Order order = Order.builder()
                .restaurant(restaurant)
                .dishes(List.of(orderDish))
                .build();

        when(orderPersistencePort.customerHasOrdersInProcess(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(Restaurant.builder().id(restaurantId).build()));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderUseCase.createOrder(order));
    }

    @Test
    void createOrder_shouldThrowRestaurantNotFoundException_whenRestaurantNotFound() {
        Long restaurantId = 10L;
        OrderDish orderDish = new OrderDish(100L, 1);
        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
        Order order = Order.builder()
                .restaurant(restaurant)
                .dishes(List.of(orderDish))
                .build();

        when(orderPersistencePort.customerHasOrdersInProcess(anyLong())).thenReturn(false);
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(1L));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> orderUseCase.createOrder(order));
    }

    @Test
    void createOrder_shouldThrowDishNotFoundException_whenDishNotFound() {
        Long customerId = 1L;
        Long restaurantId = 10L;
        OrderDish orderDish = new OrderDish(100L, 1);
        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
        Order order = Order.builder()
                .restaurant(restaurant)
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
        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
        Order order = Order.builder()
                .restaurant(restaurant)
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

    @Test
    void markOrderAsReady_shouldGeneratePinUpdateOrderAndSendSms() {
        Long orderId = 1L;
        Long customerId = 100L;
        String phone = "+123456789";

        Order existingOrder = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.IN_PREPARATION)
                .securityPin(null)
                .build();

        User user = User.builder()
                .phoneNumber(phone)
                .build();

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(existingOrder));
        when(userServiceClientPort.findUserById(customerId)).thenReturn(user);

        orderUseCase.markOrderAsReady(orderId);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).updateOrder(orderCaptor.capture());
        Order updatedOrder = orderCaptor.getValue();

        assertEquals(OrderStatus.READY, updatedOrder.getStatus());
        assertNotNull(updatedOrder.getSecurityPin());
        assertEquals(4, updatedOrder.getSecurityPin().length());

        String expectedMessage = "Your order is ready. Use this PIN to claim it: " + updatedOrder.getSecurityPin();
        verify(sendSmsPort).sendSms(eq(phone), eq(expectedMessage));
    }

    @Test
    void markOrderAsReady_shouldThrowWhenOrderNotFound() {
        Long orderId = 1L;
        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            orderUseCase.markOrderAsReady(orderId);
        });

        verifyNoInteractions(sendSmsPort, userServiceClientPort);
    }

    @Test
    void markOrderAsReady_shouldThrowWhenOrderNotInPreparation() {
        Long orderId = 1L;
        Order order = Order.builder()
                .id(orderId)
                .customerId(10L)
                .status(OrderStatus.PENDING)
                .securityPin(null)
                .build();
        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> {
            orderUseCase.markOrderAsReady(orderId);
        });

        verifyNoInteractions(sendSmsPort, userServiceClientPort);
    }

    @Test
    void markOrderAsDelivered_Successful() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .securityPin("1234")
                .build();

        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.of(order));

        orderUseCase.markOrderAsDelivered(1L, "1234");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).updateOrder(orderCaptor.capture());

        Order updated = orderCaptor.getValue();
        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
        assertEquals(order.getId(), updated.getId());
        assertEquals(order.getSecurityPin(), updated.getSecurityPin());    }

    @Test
    void markOrderAsDelivered_OrderNotFound_ThrowsException() {
        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderUseCase.markOrderAsDelivered(1L, "1234"));
    }

    @Test
    void markOrderAsDelivered_InvalidStatus_ThrowsException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.IN_PREPARATION)
                .securityPin("1234")
                .build();

        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () ->
                orderUseCase.markOrderAsDelivered(1L, "1234"));
    }

    @Test
    void markOrderAsDelivered_WrongPin_ThrowsException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .securityPin("1234")
                .build();

        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(InvalidSecurityPinException.class, () ->
                orderUseCase.markOrderAsDelivered(1L, "0000"));
    }
}