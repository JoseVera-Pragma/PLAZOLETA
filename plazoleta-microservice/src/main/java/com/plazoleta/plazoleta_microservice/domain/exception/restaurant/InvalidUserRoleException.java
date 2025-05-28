package com.plazoleta.plazoleta_microservice.domain.exception.restaurant;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidUserRoleException extends DomainException {
    public InvalidUserRoleException(String message) {
        super(message);
    }
}
