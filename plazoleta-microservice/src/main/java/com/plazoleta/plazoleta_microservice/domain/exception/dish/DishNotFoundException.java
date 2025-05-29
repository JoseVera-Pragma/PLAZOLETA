package com.plazoleta.plazoleta_microservice.domain.exception.dish;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class DishNotFoundException extends DomainException {
    public DishNotFoundException(String message) {
        super(message);
    }
}
