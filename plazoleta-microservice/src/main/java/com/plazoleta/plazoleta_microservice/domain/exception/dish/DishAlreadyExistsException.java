package com.plazoleta.plazoleta_microservice.domain.exception.dish;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class DishAlreadyExistsException extends DomainException {
    public DishAlreadyExistsException(String message) {
        super(message);
    }
}
