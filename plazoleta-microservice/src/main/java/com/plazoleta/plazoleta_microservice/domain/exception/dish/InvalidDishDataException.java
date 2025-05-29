package com.plazoleta.plazoleta_microservice.domain.exception.dish;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidDishDataException extends DomainException {
    public InvalidDishDataException(String message) {
        super(message);
    }
}
