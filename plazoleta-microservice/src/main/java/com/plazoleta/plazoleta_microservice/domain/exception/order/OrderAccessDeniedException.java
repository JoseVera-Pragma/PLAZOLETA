package com.plazoleta.plazoleta_microservice.domain.exception.order;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class OrderAccessDeniedException extends DomainException {
    public OrderAccessDeniedException(String message) {
        super(message);
    }
}
