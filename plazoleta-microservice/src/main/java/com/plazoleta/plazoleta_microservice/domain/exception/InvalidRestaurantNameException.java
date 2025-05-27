package com.plazoleta.plazoleta_microservice.domain.exception;

public class InvalidRestaurantNameException extends DomainException {
    public InvalidRestaurantNameException(String message) {
        super(message);
    }
}
