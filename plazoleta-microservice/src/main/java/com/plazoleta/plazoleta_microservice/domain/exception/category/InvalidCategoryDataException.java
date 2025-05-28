package com.plazoleta.plazoleta_microservice.domain.exception.category;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;

public class InvalidCategoryDataException extends DomainException {
    public InvalidCategoryDataException(String message) {
        super(message);
    }
}
