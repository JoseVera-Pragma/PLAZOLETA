package com.plazoleta.plazoleta_microservice.domain.exception.dish;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class UnauthorizedOwnerException extends DomainException {
    public UnauthorizedOwnerException(String message) {
        super(message);
    }
}
