package com.plazoleta.plazoleta_microservice.domain.exception;

public class InvalidRestaurantOwnerIdException extends DomainException {
    public InvalidRestaurantOwnerIdException(String message) {
        super(message);
    }
}
