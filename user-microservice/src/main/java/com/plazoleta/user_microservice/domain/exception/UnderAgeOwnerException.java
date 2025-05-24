package com.plazoleta.user_microservice.domain.exception;

public class UnderAgeOwnerException extends RuntimeException {
    public UnderAgeOwnerException(String message) {
        super(message);
    }
}
