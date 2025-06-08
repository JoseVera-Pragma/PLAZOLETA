package com.plazoleta.plazoleta_microservice.domain.exception.order;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class OrderInProcessException extends DomainException {
    public OrderInProcessException(String message) {
        super(message);
    }
}
