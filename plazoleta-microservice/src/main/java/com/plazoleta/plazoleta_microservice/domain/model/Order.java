package com.plazoleta.plazoleta_microservice.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final Long id;
    private final Long customerId;
    private final LocalDateTime orderDate;
    private final OrderStatus status;
    private final Long chefId;
    private final Restaurant restaurant;
    private final List<OrderDish> dishes;
    private final String securityPin;


    private Order(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.orderDate = builder.orderDate;
        this.status = builder.status;
        this.chefId = builder.chefId;
        this.restaurant = builder.restaurant;
        this.dishes = builder.dishes;
        this.securityPin = builder.securityPin;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long customerId;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Long chefId;
        private Restaurant restaurant;
        private List<OrderDish> dishes;
        private String securityPin;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder chefId(Long chefId) {
            this.chefId = chefId;
            return this;
        }

        public Builder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public Builder dishes(List<OrderDish> dishes) {
            this.dishes = dishes;
            return this;
        }

        public Builder securityPin(String securityPin) {
            this.securityPin = securityPin;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    public Order withCustomerId(Long customerId) {
        return Order.builder()
                .id(this.id)
                .restaurant(this.restaurant)
                .chefId(this.chefId)
                .dishes(this.dishes)
                .customerId(customerId)
                .orderDate(this.orderDate)
                .status(this.status)
                .securityPin(this.securityPin)
                .build();
    }

    public Order withChefId(Long chefId){
        return Order.builder()
                .id(this.id)
                .restaurant(this.restaurant)
                .chefId(chefId)
                .dishes(this.dishes)
                .customerId(this.customerId)
                .orderDate(this.orderDate)
                .status(this.status)
                .securityPin(this.securityPin)
                .build();
    }

    public Order withOrderDate(LocalDateTime orderDate){
        return Order.builder()
                .id(this.id)
                .restaurant(this.restaurant)
                .chefId(this.chefId)
                .dishes(this.dishes)
                .customerId(this.customerId)
                .orderDate(orderDate)
                .status(this.status)
                .securityPin(this.securityPin)
                .build();
    }

    public Order withStatus(OrderStatus status){
        return Order.builder()
                .id(this.id)
                .restaurant(this.restaurant)
                .chefId(this.chefId)
                .dishes(this.dishes)
                .customerId(this.customerId)
                .orderDate(this.orderDate)
                .status(status)
                .securityPin(this.securityPin)
                .build();
    }

    public Order withRestaurant(Restaurant restaurant){
        return Order.builder()
                .id(this.id)
                .restaurant(restaurant)
                .chefId(this.chefId)
                .dishes(this.dishes)
                .customerId(this.customerId)
                .orderDate(this.orderDate)
                .status(this.status)
                .securityPin(this.securityPin)
                .build();
    }

    public Order withDishes(List<OrderDish> dishes){
        return Order.builder()
                .id(this.id)
                .restaurant(this.restaurant)
                .chefId(this.chefId)
                .dishes(dishes)
                .customerId(this.customerId)
                .orderDate(this.orderDate)
                .status(this.status)
                .securityPin(this.securityPin)
                .build();
    }

    public Order withSecurityPin(String securityPin){
        return Order.builder()
                .id(this.id)
                .restaurant(this.restaurant)
                .chefId(this.chefId)
                .dishes(this.dishes)
                .customerId(this.customerId)
                .orderDate(this.orderDate)
                .status(this.status)
                .securityPin(securityPin)
                .build();
    }

    public Long getChefId() {
        return chefId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public List<OrderDish> getDishes() {
        return dishes;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getSecurityPin(){
        return securityPin;
    }
}
