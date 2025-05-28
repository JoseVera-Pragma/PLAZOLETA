package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidRestaurantNameException extends DomainException {
    public InvalidRestaurantNameException(String message) {
        super(message);
    }
}
