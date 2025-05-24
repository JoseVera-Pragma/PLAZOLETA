package com.plazoleta.user_microservice.domain.exception;

public class RoleInUseException extends RuntimeException {
    public RoleInUseException(String message) {
        super(message);
    }
}
