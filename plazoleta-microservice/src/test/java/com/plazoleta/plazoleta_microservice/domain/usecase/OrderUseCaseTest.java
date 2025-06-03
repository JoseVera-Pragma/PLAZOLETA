package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.model.*;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IOrderPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderUseCaseTest {
    private IOrderPersistencePort orderPersistencePort;
    private IDishPersistencePort dishPersistencePort;
    private IRestaurantPersistencePort restaurantPersistencePort;
    private OrderUseCase orderUseCase;

    private final Category category = new Category(1L, "Postre", "Descripción postres");

    @BeforeEach
    void setUp() {
        orderPersistencePort = mock(IOrderPersistencePort.class);
        dishPersistencePort = mock(IDishPersistencePort.class);
        restaurantPersistencePort = mock(IRestaurantPersistencePort.class);
        orderUseCase = new OrderUseCase(orderPersistencePort, dishPersistencePort, restaurantPersistencePort);
    }

    @Test
    void testCreateOrder_ShouldThrowIfCustomerHasOrderInProcess() {
        Order order = new Order(1L, 10L, null, 100L, null, null, List.of());

        when(orderPersistencePort.customerHasOrdersInProcess(10L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderUseCase.createOrder(order);
        });

        assertEquals("Customer have order in process.", exception.getMessage());
    }

    @Test
    void testCreateOrder_ShouldThrowIfDishNotFromRestaurant() {
        OrderDish orderDish = new OrderDish(1L, 2);
        Order order = new Order(1L, 10L, null, 100L, null, null, List.of(orderDish));

        when(orderPersistencePort.customerHasOrdersInProcess(10L)).thenReturn(false);
        when(dishPersistencePort.getById(1L)).thenReturn(new Dish.Builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("Desc")
                .active(true)
                .imageUrl("fds")
                .restaurantId(200L)
                .category(category)
                .build());

        assertThrows(IllegalArgumentException.class, () -> orderUseCase.createOrder(order));
    }

    @Test
    void testCreateOrder_ShouldSaveOrderWithStatusPendingAndDate() {
        OrderDish orderDish = new OrderDish(1L, 2);
        Order order = new Order(1L, 10L, null, 100L, null, null, List.of(orderDish));

        when(orderPersistencePort.customerHasOrdersInProcess(10L)).thenReturn(false);
        when(dishPersistencePort.getById(1L)).thenReturn(new Dish.Builder()
                .id(1L)
                .name("Dish")
                .price(1000.0)
                .description("Desc")
                .active(true)
                .imageUrl("fds")
                .restaurantId(100L)
                .category(category)
                .build());

        Restaurant restaurant = new Restaurant.Builder()
                .idOwner(1L)
                .name("Valid Restaurant")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .build();
        when(restaurantPersistencePort.getById(100L)).thenReturn(restaurant);

        orderUseCase.createOrder(order);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).saveOrder(captor.capture());

        Order savedOrder = captor.getValue();

        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertNotNull(savedOrder.getOrderDate());
        assertEquals(10L, savedOrder.getCustomerId());
    }

    @Test
    void testGetOrdersByClientId_ShouldReturnList() {
        Long clientId = 20L;
        List<Order> orders = List.of(new Order(1L, clientId, null, 100L, LocalDateTime.now(), OrderStatus.PENDING, List.of()));

        when(orderPersistencePort.getOrdersByCustomerId(clientId)).thenReturn(orders);

        List<Order> result = orderUseCase.getOrdersByClientId(clientId);

        assertEquals(orders, result);
    }

    @Test
    void testGetOrderById_ShouldReturnOrder() {
        Order order = new Order(1L, 10L, null, 100L, LocalDateTime.now(), OrderStatus.PENDING, List.of());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);

        Order result = orderUseCase.getOrderById(1L);

        assertEquals(order, result);
    }

    @Test
    void getOrdersByStatusAndRestaurantId_ShouldReturnOrdersList() {
        Long restaurantId = 1L;
        OrderStatus status = OrderStatus.PENDING;
        int pageIndex = 0;
        int elementsPerPage = 5;

        Restaurant restaurant = new Restaurant.Builder()
                .idOwner(1L)
                .name("Valid Restaurant")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .build();
        List<Order> expectedOrders = List.of(
                new Order(), new Order()
        );

        when(restaurantPersistencePort.getById(restaurantId)).thenReturn(restaurant);

        when(orderPersistencePort.getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage))
                .thenReturn(expectedOrders);

        List<Order> result = orderUseCase.getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage);

        assertEquals(expectedOrders, result);
        verify(restaurantPersistencePort).getById(restaurantId);
        verify(orderPersistencePort).getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage);
    }
}