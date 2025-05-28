package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidRestaurantUrlLogoException extends DomainException {
    public InvalidRestaurantUrlLogoException(String message) {
        super(message);
    }
}
