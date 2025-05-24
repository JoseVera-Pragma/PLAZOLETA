package com.plazoleta.user_microservice.domain.exception;

public class RoleNotAllowedException extends RuntimeException {
    public RoleNotAllowedException(String message) {
        super(message);
    }
}
