package com.plazoleta.plazoleta_microservice.infrastructure.exception;

public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
