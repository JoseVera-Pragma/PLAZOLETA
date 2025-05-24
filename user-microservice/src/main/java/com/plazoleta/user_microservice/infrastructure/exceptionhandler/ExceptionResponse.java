package com.plazoleta.user_microservice.infrastructure.exceptionhandler;

public enum ExceptionResponse {
    ROLE_ASSIGNED_EXCEPTION("Cannot delete: Role is assigned to one or more users.");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
