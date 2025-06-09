package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.InvalidSecurityPinException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.InvalidOrderStatusException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderAccessDeniedException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderInProcessException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.*;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    @Mock
    private ITraceabilityClientPort traceabilityClientPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    @Test
    void createOrder_shouldSaveOrder_whenValid() {
        Long customerId = 1L;
        Long restaurantId = 10L;
        Long dishId = 100L;

        Dish dish = Dish.builder().id(100L).restaurantId(restaurantId).build();
        OrderDish orderDish = new OrderDish(100L, 1);
        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
        User user = User.builder().email("test@example.com").build();
        Order order = Order.builder()
                .restaurant(restaurant)
                .dishes(List.of(orderDish))
                .build();

        Order orderToSave = order.withCustomerId(customerId)
                .withStatus(OrderStatus.PENDING)
                .withDishes(List.of(orderDish))
                .withRestaurant(restaurant);

        Order savedOrder = orderToSave.builder().id(1L).build();

        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));
        when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(savedOrder);
        when(userServiceClientPort.findUserById(customerId)).thenReturn(user);
        doNothing().when(traceabilityClientPort).saveTraceability(any(Traceability.class));

        orderUseCase.createOrder(order);

        verify(orderPersistencePort).saveOrder(argThat(saved ->
                saved.getCustomerId().equals(customerId)
                        && saved.getStatus() == OrderStatus.PENDING
                        && saved.getRestaurant().equals(restaurant)
                        && saved.getDishes().size() == 1
        ));

        verify(traceabilityClientPort).saveTraceability(argThat(trace ->
                trace.getOrderId().equals(savedOrder.getId())
                        && trace.getCustomerId().equals(customerId)
                        && trace.getCustomerEmail().equals("test@example.com")
                        && trace.getNewState().equals(OrderStatus.PENDING.name())
        ));
    }

    @Test
    void createOrder_shouldOrderInProcessException_whenCustomerHasOrdersInProcess() {
        Long customerId = 1L;
        Order order = Order.builder().customerId(customerId).build();

        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(orderPersistencePort.customerHasOrdersInProcess(customerId)).thenReturn(true);

        assertThrows(OrderInProcessException.class, () -> orderUseCase.createOrder(order));
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
        Long customerId = 20L;

        Order existingOrder = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .customerId(customerId)
                .build();

        Order updatedOrder = existingOrder.withChefId(employedId).withStatus(OrderStatus.IN_PREPARATION);

        User customer = User.builder().email("customer@example.com").build();
        User employed = User.builder().email("chef@example.com").build();


        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(existingOrder));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(updatedOrder);
        when(userServiceClientPort.findUserById(customerId)).thenReturn(customer);
        when(userServiceClientPort.findUserById(employedId)).thenReturn(employed);
        doNothing().when(traceabilityClientPort).saveTraceability(any(Traceability.class));

        orderUseCase.assignOrder(orderId);

        verify(orderPersistencePort).updateOrder(argThat(updated ->
                updated.getChefId().equals(employedId) &&
                        updated.getStatus() == OrderStatus.IN_PREPARATION
        ));

        verify(traceabilityClientPort).saveTraceability(argThat(trace ->
                trace.getOrderId().equals(orderId) &&
                        trace.getCustomerId().equals(customerId) &&
                        trace.getCustomerEmail().equals("customer@example.com") &&
                        trace.getPreviousState().equals(OrderStatus.PENDING.name()) &&
                        trace.getNewState().equals(OrderStatus.IN_PREPARATION.name()) &&
                        trace.getEmployedId().equals(employedId) &&
                        trace.getEmployedEmail().equals("chef@example.com")
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
    void assignOrder_shouldInvalidOrderStatusException_whenUserNotFound() {
        Long orderId = 5L;
        Long employedId = 1L;
        Order order = Order.builder().id(orderId).status(OrderStatus.IN_PREPARATION).build();

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));

        assertThrows(InvalidOrderStatusException.class, () -> orderUseCase.assignOrder(orderId));
    }

    @Test
    void markOrderAsReady_shouldGeneratePinUpdateOrderAndSendSms() {
        Long orderId = 1L;
        Long customerId = 100L;
        Long employedId = 10L;
        String customerPhone = "+123456789";
        String customerEmail = "customer@example.com";
        String employedEmail = "employee@example.com";

        Order existingOrder = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.IN_PREPARATION)
                .securityPin(null)
                .build();

        User customer = User.builder()
                .phoneNumber(customerPhone)
                .email(customerEmail)
                .build();

        User employed = User.builder()
                .email(employedEmail)
                .build();

        Order updatedOrderMock = existingOrder
                .withStatus(OrderStatus.READY)
                .withSecurityPin("1234");

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(existingOrder));
        when(userServiceClientPort.findUserById(customerId)).thenReturn(customer);
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));
        when(userServiceClientPort.findUserById(employedId)).thenReturn(employed);
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(updatedOrderMock);

        orderUseCase.markOrderAsReady(orderId);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).updateOrder(orderCaptor.capture());
        Order updatedOrder = orderCaptor.getValue();

        assertEquals(OrderStatus.READY, updatedOrder.getStatus());
        assertNotNull(updatedOrder.getSecurityPin());
        assertEquals(4, updatedOrder.getSecurityPin().length());

        String expectedMessage = "Your order is ready. Use this PIN to claim it: " + updatedOrder.getSecurityPin();
        verify(sendSmsPort).sendSms(customerPhone,expectedMessage);

        verify(traceabilityClientPort).saveTraceability(argThat(trace ->
                trace.getOrderId().equals(orderId) &&
                        trace.getCustomerId().equals(customerId) &&
                        trace.getCustomerEmail().equals(customerEmail) &&
                        trace.getPreviousState().equals(OrderStatus.IN_PREPARATION.name()) &&
                        trace.getNewState().equals(OrderStatus.READY.name()) &&
                        trace.getEmployedId().equals(employedId) &&
                        trace.getEmployedEmail().equals(employedEmail)
        ));
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
        Long orderId = 1L;
        Long customerId = 100L;
        Long employedId = 200L;
        String pin = "1234";

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.READY)
                .securityPin(pin)
                .build();

        Order updatedOrderMock = order.withStatus(OrderStatus.DELIVERED);

        User customer = User.builder()
                .email("customer@example.com")
                .build();

        User employed = User.builder()
                .email("employed@example.com")
                .build();

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(updatedOrderMock);
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(employedId));
        when(userServiceClientPort.findUserById(employedId)).thenReturn(employed);
        when(userServiceClientPort.findUserById(customerId)).thenReturn(customer);

        orderUseCase.markOrderAsDelivered(orderId, pin);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).updateOrder(orderCaptor.capture());

        Order updated = orderCaptor.getValue();
        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
        assertEquals(order.getId(), updated.getId());
        assertEquals(order.getSecurityPin(), updated.getSecurityPin());

        verify(traceabilityClientPort).saveTraceability(argThat(trace ->
                trace.getOrderId().equals(orderId) &&
                        trace.getCustomerId().equals(customerId) &&
                        trace.getCustomerEmail().equals("customer@example.com") &&
                        trace.getPreviousState().equals(OrderStatus.READY.name()) &&
                        trace.getNewState().equals(OrderStatus.DELIVERED.name()) &&
                        trace.getEmployedId().equals(employedId) &&
                        trace.getEmployedEmail().equals("employed@example.com")
        ));
    }

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

    @Test
    void markOrderAsCanceled_Successful() {
        Long orderId = 1L;
        Long customerId = 1L;

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .build();

        Order updatedOrderMock = order.withStatus(OrderStatus.CANCELLED);

        User customer = User.builder()
                .email("customer@example.com")
                .build();

        when(orderPersistencePort.findOrderById(orderId)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(updatedOrderMock);
        when(userServiceClientPort.findUserById(customerId)).thenReturn(customer);

        orderUseCase.markOrderAsCanceled(orderId);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).updateOrder(orderCaptor.capture());

        Order updated = orderCaptor.getValue();
        assertEquals(OrderStatus.CANCELLED, updated.getStatus());
        assertEquals(order.getId(), updated.getId());

        verify(traceabilityClientPort).saveTraceability(argThat(trace ->
                trace.getOrderId().equals(orderId) &&
                        trace.getCustomerId().equals(customerId) &&
                        trace.getCustomerEmail().equals("customer@example.com") &&
                        trace.getPreviousState().equals(OrderStatus.PENDING.name()) &&
                        trace.getNewState().equals(OrderStatus.CANCELLED.name())
        ));
    }

    @Test
    void markOrderAsCanceled_OrderNotFound_ThrowsException() {
        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderUseCase.markOrderAsCanceled(1L));
    }

    @Test
    void markOrderAsCanceled_InvalidOrderStatus_ThrowsException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .build();

        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () ->
                orderUseCase.markOrderAsCanceled(1L));
    }

    @Test
    void markOrderAsCanceled_UserNotFound_ThrowsException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .customerId(1L)
                .build();

        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.of(order));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                orderUseCase.markOrderAsCanceled(1L));
    }

    @Test
    void markOrderAsCanceled_OrderAccessDenied_ThrowsException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .customerId(1L)
                .build();

        when(orderPersistencePort.findOrderById(1L))
                .thenReturn(Optional.of(order));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(2L));

        assertThrows(OrderAccessDeniedException.class, () ->
                orderUseCase.markOrderAsCanceled(1L));
    }
}