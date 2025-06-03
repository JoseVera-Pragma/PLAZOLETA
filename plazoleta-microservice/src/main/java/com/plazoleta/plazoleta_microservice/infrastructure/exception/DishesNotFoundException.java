package com.plazoleta.plazoleta_microservice.infrastructure.exception;

public class DishesNotFoundException extends RuntimeException {
    public DishesNotFoundException(String message) {
        super(message);
    }
}
