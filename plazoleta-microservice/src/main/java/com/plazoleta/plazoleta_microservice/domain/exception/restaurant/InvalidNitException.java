package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidNitException extends DomainException {
    public InvalidNitException(String message) {
        super(message);
    }
}
