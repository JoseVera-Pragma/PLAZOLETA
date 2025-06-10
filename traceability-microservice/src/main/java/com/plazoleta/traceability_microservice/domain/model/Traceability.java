package com.plazoleta.traceability_microservice.domain.model;

import java.time.LocalDateTime;

public class Traceability {
    private final String id;
    private final Long restaurantId;
    private final Long orderId;
    private final Long customerId;
    private final String customerEmail;
    private final LocalDateTime date;
    private final String previousState;
    private final String newState;
    private final Long employedId;
    private final String employedEmail;

    private Traceability(Builder builder) {
        this.id = builder.id;
        this.restaurantId = builder.restaurantId;
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.customerEmail = builder.customerEmail;
        this.date = builder.date;
        this.previousState = builder.previousState;
        this.newState = builder.newState;
        this.employedId = builder.employedId;
        this.employedEmail = builder.employedEmail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Long restaurantId;
        private Long orderId;
        private Long customerId;
        private String customerEmail;
        private LocalDateTime date;
        private String previousState;
        private String newState;
        private Long employedId;
        private String employedEmail;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder restaurantId(Long restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Builder previousState(String previousState) {
            this.previousState = previousState;
            return this;
        }

        public Builder newState(String newState) {
            this.newState = newState;
            return this;
        }

        public Builder employedId(Long employedId) {
            this.employedId = employedId;
            return this;
        }

        public Builder employedEmail(String employedEmail) {
            this.employedEmail = employedEmail;
            return this;
        }

        public Traceability build() {
            return new Traceability(this);
        }
    }

    public Traceability whitDate(LocalDateTime date) {
        return Traceability.builder()
                .id(this.id)
                .restaurantId(this.restaurantId)
                .orderId(this.orderId)
                .customerId(this.customerId)
                .customerEmail(this.customerEmail)
                .date(date)
                .previousState(this.previousState)
                .newState(this.newState)
                .employedId(this.employedId)
                .employedEmail(this.employedEmail)
                .build();
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getEmployedEmail() {
        return employedEmail;
    }

    public Long getEmployedId() {
        return employedId;
    }

    public String getId() {
        return id;
    }

    public String getNewState() {
        return newState;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getPreviousState() {
        return previousState;
    }
}
