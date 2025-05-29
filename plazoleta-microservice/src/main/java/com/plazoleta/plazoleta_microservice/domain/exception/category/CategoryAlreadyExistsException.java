package com.plazoleta.plazoleta_microservice.domain.exception.category;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class CategoryAlreadyExistsException extends DomainException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
