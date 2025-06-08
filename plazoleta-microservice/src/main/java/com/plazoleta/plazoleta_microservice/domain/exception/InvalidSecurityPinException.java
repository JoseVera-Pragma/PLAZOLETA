package com.plazoleta.plazoleta_microservice.domain.exception;

public class InvalidSecurityPinException extends DomainException {
    public InvalidSecurityPinException(String message) {
        super(message);
    }
}
