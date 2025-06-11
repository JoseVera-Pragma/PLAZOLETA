package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.InvalidSecurityPinException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.InvalidOrderStatusException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderAccessDeniedException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderInProcessException;
import com.plazoleta.plazoleta_microservice.domain.exception.order.OrderNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.*;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IUserServiceClientPort userServiceClientPort;
    private final ISendSmsPort sendSmsPort;
    private final ITraceabilityClientPort traceabilityClientPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IAuthenticatedUserPort authenticatedUserPort,
                        IUserServiceClientPort userServiceClientPort, ISendSmsPort sendSmsPort, ITraceabilityClientPort traceabilityClientPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.userServiceClientPort = userServiceClientPort;
        this.sendSmsPort = sendSmsPort;
        this.traceabilityClientPort = traceabilityClientPort;
    }

    @Override
    public void createOrder(Order order) {
        Long customerId = authenticatedUserPort.getCurrentUserId()
                .orElseThrow(() ->
                        new UserNotFoundException("User not found.")
                );

        if (orderPersistencePort.customerHasOrdersInProcess(customerId)) {
            throw new OrderInProcessException("Customer have order in process.");
        }

        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(order.getRestaurant().getId())
                .orElseThrow(() ->
                        new RestaurantNotFoundException("Restaurant whit ID " + order.getRestaurant().getId() + " not found.")
                );

        List<OrderDish> orderDishes = validateAndEnrichOrderDishes(order.getDishes(), order.getRestaurant().getId());

        Order orderToSave = order.withCustomerId(customerId)
                .withOrderDate(LocalDateTime.now())
                .withStatus(OrderStatus.PENDING)
                .withDishes(orderDishes)
                .withRestaurant(restaurant);

        Order orderSaved = orderPersistencePort.saveOrder(orderToSave);

        User customer = userServiceClientPort.findUserById(customerId);

        Traceability traceability = Traceability.builder()
                .restaurantId(orderSaved.getRestaurant().getId())
                .orderId(orderSaved.getId())
                .customerId(customerId)
                .customerEmail(customer.getEmail())
                .newState(OrderStatus.PENDING.name())
                .build();

        traceabilityClientPort.saveTraceability(traceability);
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderPersistencePort.findOrdersByCustomerId(customerId);
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order with ID " + orderId + " not found.")
                );
    }

    @Override
    public Page<Order> findOrdersByStatusForAuthenticatedEmployee(OrderStatus status, int pageIndex, int elementsPerPage) {
        Long employedId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        Long associatedRestaurantId = userServiceClientPort.findUserById(employedId).getRestaurantId();

        restaurantPersistencePort.findRestaurantById(associatedRestaurantId)
                .orElseThrow(() ->
                        new RestaurantNotFoundException("Restaurant with ID " + associatedRestaurantId + " not found.")
                );

        return orderPersistencePort.findOrdersByStatusAndRestaurantId(associatedRestaurantId, status, pageIndex, elementsPerPage);
    }

    @Override
    public void assignOrder(Long orderId) {
        Order order = orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order with ID " + orderId + " not found.")
                );

        Long employedId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStatusException("Order is not pending");
        }

        Order orderToUpdated = order.withChefId(employedId).withStatus(OrderStatus.IN_PREPARATION);
        Order orderUpdated = orderPersistencePort.updateOrder(orderToUpdated);

        User customer = userServiceClientPort.findUserById(orderUpdated.getCustomerId());
        User employed = userServiceClientPort.findUserById(employedId);

        Traceability traceability = Traceability.builder()
                .restaurantId(orderUpdated.getRestaurant().getId())
                .orderId(orderUpdated.getId())
                .customerId(orderUpdated.getCustomerId())
                .customerEmail(customer.getEmail())
                .previousState(OrderStatus.PENDING.name())
                .newState(OrderStatus.IN_PREPARATION.name())
                .employedId(employedId)
                .employedEmail(employed.getEmail())
                .build();

        traceabilityClientPort.saveTraceability(traceability);
    }

    private List<OrderDish> validateAndEnrichOrderDishes(List<OrderDish> orderDishes, Long restaurantId) {
        List<OrderDish> enrichedOrderDishes = new ArrayList<>();

        for (OrderDish dish : orderDishes) {
            Dish fullDish = dishPersistencePort.findDishById(dish.getDishId())
                    .orElseThrow(() ->
                            new DishNotFoundException("Dish with ID " + dish.getDishId() + " not found.")
                    );

            if (!fullDish.getRestaurantId().equals(restaurantId)) {
                throw new IllegalArgumentException("All dishes must belong to the same restaurant.");
            }

            enrichedOrderDishes.add(new OrderDish(fullDish.getId(), dish.getQuantity()));
        }

        return enrichedOrderDishes;
    }

    @Override
    public void markOrderAsReady(Long orderId) {
        Order order = orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order with ID " + orderId + " not found.")
                );

        if (!order.getStatus().equals(OrderStatus.IN_PREPARATION)) {
            throw new InvalidOrderStatusException("Order is not preparing");
        }

        String pin = generatePin();
        Order updatedOrder = order.withSecurityPin(pin).withStatus(OrderStatus.READY);

        Order orderUpdated = orderPersistencePort.updateOrder(updatedOrder);

        User customer = userServiceClientPort.findUserById(order.getCustomerId());
        String message = "Your order is ready. Use this PIN to claim it: " + pin;
        sendSmsPort.sendSms(customer.getPhoneNumber(), message);

        Long employedId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        User employed = userServiceClientPort.findUserById(employedId);

        Traceability traceability = Traceability.builder()
                .restaurantId(orderUpdated.getRestaurant().getId())
                .orderId(orderUpdated.getId())
                .customerId(orderUpdated.getCustomerId())
                .customerEmail(customer.getEmail())
                .previousState(OrderStatus.IN_PREPARATION.name())
                .newState(OrderStatus.READY.name())
                .employedId(employedId)
                .employedEmail(employed.getEmail())
                .build();

        traceabilityClientPort.saveTraceability(traceability);
    }

    @Override
    public void markOrderAsDelivered(Long orderId, String securityPin) {
        Order order = orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order with ID " + orderId + " not found.")
                );

        if (!order.getStatus().equals(OrderStatus.READY)) {
            throw new InvalidOrderStatusException("Order is not ready");
        }

        if (!order.getSecurityPin().equals(securityPin)) {
            throw new InvalidSecurityPinException("Security pin is not valid");
        }

        Order updatedOrder = order.withStatus(OrderStatus.DELIVERED);

        Order orderUpdated = orderPersistencePort.updateOrder(updatedOrder);

        Long employedId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        User employed = userServiceClientPort.findUserById(employedId);

        User customer = userServiceClientPort.findUserById(orderUpdated.getCustomerId());

        Traceability traceability = Traceability.builder()
                .restaurantId(orderUpdated.getRestaurant().getId())
                .orderId(orderUpdated.getId())
                .customerId(orderUpdated.getCustomerId())
                .customerEmail(customer.getEmail())
                .previousState(OrderStatus.READY.name())
                .newState(OrderStatus.DELIVERED.name())
                .employedId(employedId)
                .employedEmail(employed.getEmail())
                .build();

        traceabilityClientPort.saveTraceability(traceability);
    }

    @Override
    public void markOrderAsCanceled(Long orderId) {
        Order order = orderPersistencePort.findOrderById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order with ID " + orderId + " not found.")
                );

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStatusException("Sorry, your order is already in preparation and cannot be canceled");
        }

        Long customerId = authenticatedUserPort.getCurrentUserId()
                .orElseThrow(() ->
                        new UserNotFoundException("User not found.")
                );

        if (!order.getCustomerId().equals(customerId)) {
            throw new OrderAccessDeniedException("You do not have permission to access this order.");
        }

        Order updatedOrder = order.withStatus(OrderStatus.CANCELLED);

        Order orderUpdated = orderPersistencePort.updateOrder(updatedOrder);

        User customer = userServiceClientPort.findUserById(orderUpdated.getCustomerId());

        Traceability traceability = Traceability.builder()
                .restaurantId(orderUpdated.getRestaurant().getId())
                .orderId(orderUpdated.getId())
                .customerId(orderUpdated.getCustomerId())
                .customerEmail(customer.getEmail())
                .previousState(OrderStatus.PENDING.name())
                .newState(OrderStatus.CANCELLED.name())
                .build();

        traceabilityClientPort.saveTraceability(traceability);
    }

    private String generatePin() {
        Random random = new Random();
        int pin = 1000 + random.nextInt(9000);
        return String.valueOf(pin);
    }
}
