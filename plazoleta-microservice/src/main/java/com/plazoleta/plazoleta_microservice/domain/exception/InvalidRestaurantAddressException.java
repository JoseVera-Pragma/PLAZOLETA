package com.plazoleta.plazoleta_microservice.domain.exception;

public class InvalidRestaurantAddressException extends DomainException {
    public InvalidRestaurantAddressException(String message) {
        super(message);
    }
}
