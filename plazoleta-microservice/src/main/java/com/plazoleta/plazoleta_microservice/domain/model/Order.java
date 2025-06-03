package com.plazoleta.plazoleta_microservice.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long chefId;
    private Long restaurantId;
    private List<OrderDish> dishes;

    public Order(){};

    public Order(Long id, Long customerId, Long chefId, Long restaurantId, LocalDateTime orderDate, OrderStatus status, List<OrderDish> dishes) {
        this.chefId = chefId;
        this.customerId = customerId;
        this.dishes = dishes;
        this.id = id;
        this.orderDate = orderDate;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    public Long getChefId() {
        return chefId;
    }

    public void setChefId(Long chefId) {
        this.chefId = chefId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderDish> getDishes() {
        return dishes;
    }

    public void setDishes(List<OrderDish> dishes) {
        this.dishes = dishes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
