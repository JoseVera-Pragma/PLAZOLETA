package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class DuplicateNitException extends DomainException {
    public DuplicateNitException(String message) {
        super(message);
    }
}
