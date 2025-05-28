package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class MissingPhoneNumberException extends DomainException {
    public MissingPhoneNumberException(String message) {
        super(message);
    }
}
