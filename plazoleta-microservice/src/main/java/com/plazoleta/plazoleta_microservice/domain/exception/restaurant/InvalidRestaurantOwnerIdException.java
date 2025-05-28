package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidRestaurantOwnerIdException extends DomainException {
    public InvalidRestaurantOwnerIdException(String message) {
        super(message);
    }
}
