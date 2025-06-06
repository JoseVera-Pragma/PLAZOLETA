package com.plazoleta.plazoleta_microservice.domain.exception.order;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidOrderStatusException extends DomainException {
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
