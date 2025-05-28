package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }
}
