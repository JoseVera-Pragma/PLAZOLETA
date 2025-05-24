package com.plazoleta.user_microservice.infrastructure.exception;

public class NotDataFoundException extends RuntimeException {
  public NotDataFoundException(String message) {
    super(message);
  }
}
