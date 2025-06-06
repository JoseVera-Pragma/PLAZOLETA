package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.OrderNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IUserServiceClientPort userServiceClientPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort, IAuthenticatedUserPort authenticatedUserPort, IUserServiceClientPort userServiceClientPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.userServiceClientPort = userServiceClientPort;
    }

    @Override
    public void createOrder(Order order) {

        if (orderPersistencePort.customerHasOrdersInProcess(order.getCustomerId())) {
            throw new IllegalStateException("Customer have order in process.");
        }

        restaurantPersistencePort.findRestaurantById(order.getRestaurantId());

        Long customerId = authenticatedUserPort.getCurrentUserId()
                .orElseThrow(() ->
                        new UserNotFoundException("User not found.")
                );

        Order orderToSave = order.withCustomerId(customerId)
                .withOrderDate(LocalDateTime.now())
                .withStatus(OrderStatus.PENDING);

        validateAllDishesFromSameRestaurant(orderToSave.getDishes(), orderToSave.getRestaurantId());

        orderPersistencePort.saveOrder(orderToSave);
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
    public List<Order> findOrdersByStatusForAuthenticatedEmployee(OrderStatus status, int pageIndex, int elementsPerPage) {
        Long employedId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        Long associatedRestaurantId  = userServiceClientPort.findUserById(employedId).getRestaurantId();

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

        Order orderUpdated = order.withChefId(employedId).withStatus(OrderStatus.IN_PREPARATION);

        orderPersistencePort.updateOrder(orderUpdated);
    }

    private void validateAllDishesFromSameRestaurant(List<OrderDish> dishes, Long restaurantId) {
        for (OrderDish dishItem : dishes) {
            Dish dish = dishPersistencePort.findDishById(dishItem.getDishId())
                    .orElseThrow(() ->
                            new DishNotFoundException("Dish with ID " + dishItem.getDishId() + " not found.")
                    );
            if (!dish.getRestaurantId().equals(restaurantId)) {
                throw new IllegalArgumentException("All dishes must belong to the same restaurant.");
            }
        }
    }


}
