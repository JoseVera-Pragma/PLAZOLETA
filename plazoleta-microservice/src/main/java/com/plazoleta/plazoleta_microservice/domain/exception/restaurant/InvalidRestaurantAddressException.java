package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidRestaurantAddressException extends DomainException {
    public InvalidRestaurantAddressException(String message) {
        super(message);
    }
}
