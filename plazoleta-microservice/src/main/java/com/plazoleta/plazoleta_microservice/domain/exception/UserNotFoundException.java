package com.plazoleta.plazoleta_microservice.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
