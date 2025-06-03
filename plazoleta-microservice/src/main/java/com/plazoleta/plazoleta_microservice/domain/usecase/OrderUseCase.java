package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderDish;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IOrderPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;

import java.time.LocalDateTime;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public void createOrder(Order order) {

        if (orderPersistencePort.customerHasOrdersInProcess(order.getCustomerId())) {
            throw new IllegalStateException("Customer have order in process.");
        }

        restaurantPersistencePort.getById(order.getRestaurantId());

        validateAllDishesFromSameRestaurant(order.getDishes(), order.getRestaurantId());

        order.setOrderDate(LocalDateTime.now());

        order.setStatus(OrderStatus.PENDING);

        orderPersistencePort.saveOrder(order);
    }

    @Override
    public List<Order> getOrdersByClientId(Long clientId) {
        return orderPersistencePort.getOrdersByCustomerId(clientId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderPersistencePort.getOrderById(orderId);
    }

    @Override
    public List<Order> getOrdersByStatusAndRestaurantId(Long restaurantId, OrderStatus status, int pageIndex, int elementsPerPage) {
        restaurantPersistencePort.getById(restaurantId);
        return orderPersistencePort.getOrdersByStatusAndRestaurantId(restaurantId, status, pageIndex, elementsPerPage);
    }

    @Override
    public void assignOrder(Long orderId, Long employedId) {
        Order order = orderPersistencePort.getOrderById(orderId);
        order.setChefId(employedId);
        order.setStatus(OrderStatus.IN_PREPARATION);
        orderPersistencePort.updateOrder(order);
    }

    private void validateAllDishesFromSameRestaurant(List<OrderDish> dishes, Long restaurantId) {
        for (OrderDish dishItem : dishes) {
            Dish dish = dishPersistencePort.getById(dishItem.getDishId());
            if (!dish.getRestaurantId().equals(restaurantId)) {
                throw new IllegalArgumentException("All dishes must belong to the same restaurant.");
            }
        }
    }


}
