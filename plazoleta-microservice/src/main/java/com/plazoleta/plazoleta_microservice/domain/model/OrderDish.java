package com.plazoleta.plazoleta_microservice.domain.model;

public class OrderDish {

    private Long orderId;
    private final Long dishId;
    private final Integer quantity;


    public OrderDish(Long dishId, Integer quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void assignOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDishId() {
        return dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
