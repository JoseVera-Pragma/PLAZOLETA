package com.plazoleta.plazoleta_microservice.domain.exception;

public class MissingPhoneNumberException extends DomainException {
    public MissingPhoneNumberException(String message) {
        super(message);
    }
}
