package com.plazoleta.plazoleta_microservice.domain.exception.category;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
