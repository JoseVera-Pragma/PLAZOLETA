package com.plazoleta.plazoleta_microservice.domain.exception.category;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class CategoryInUseException extends DomainException {
    public CategoryInUseException(String message) {
        super(message);
    }
}
