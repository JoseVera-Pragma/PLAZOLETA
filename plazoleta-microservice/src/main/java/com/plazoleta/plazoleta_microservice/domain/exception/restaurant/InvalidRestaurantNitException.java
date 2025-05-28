package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidRestaurantNitException extends DomainException {
    public InvalidRestaurantNitException(String message) {
        super(message);
    }
}
