package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class MissingNitException extends DomainException {
    public MissingNitException(String message) {
        super(message);
    }
}
