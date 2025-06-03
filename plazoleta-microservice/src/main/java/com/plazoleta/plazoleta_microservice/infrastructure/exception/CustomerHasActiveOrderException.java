package com.plazoleta.plazoleta_microservice.infrastructure.exception;

public class CustomerHasActiveOrderException extends RuntimeException {
    public CustomerHasActiveOrderException(Long customerId) {
        super("Customer with ID " + customerId + " already has an order in process.");
    }
}
