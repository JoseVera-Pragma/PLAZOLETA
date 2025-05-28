package com.plazoleta.plazoleta_microservice.infrastructure.exceptionhandler;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerAdvisorTest {


    private ControllerAdvisor advisor;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        advisor = new ControllerAdvisor();
        request = new MockHttpServletRequest("GET", "/test");
    }

    @Test
    void handleBadRequestDomainExceptions() {
        RuntimeException ex = new InvalidNitException("Invalid NIT");
        ResponseEntity<ApiError> response = advisor.handleBadRequestDomainExceptions((DomainException) ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid NIT", response.getBody().getMessage());
    }

    @Test
    void handleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<ApiError> response = advisor.handleNotFoundDomainExceptions(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleDuplicateNit() {
        DuplicateNitException ex = new DuplicateNitException("Duplicate NIT");
        ResponseEntity<ApiError> response = advisor.handleDuplicateDomainExceptions(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate NIT", response.getBody().getMessage());
    }

    @Test
    void handleValidation() {
        BindingResult result = mock(BindingResult.class);
        when(result.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "must not be null")));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, result);
        ResponseEntity<ApiError> response = advisor.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Integrity error");
        ResponseEntity<ApiError> response = advisor.handleDataIntegrityViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Data integrity violation", response.getBody().getMessage());
    }

    @Test
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("val", String.class, "param", null, new IllegalArgumentException("Invalid"));
        ResponseEntity<ApiError> response = advisor.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Invalid value"));
    }

    @Test
    void handleGeneral() {
        Exception ex = new Exception("Something went wrong");
        ResponseEntity<ApiError> response = advisor.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Unexpected error"));
    }

    @Test
    void handleTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", String.class, "id", null, new IllegalArgumentException("Invalid format")
        );

        ResponseEntity<ApiError> response = advisor.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Invalid value 'abc' for parameter 'id'"));
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleGeneral_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ApiError> response = advisor.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Unexpected error: Something went wrong"));
        assertEquals("/test", response.getBody().getPath());
    }
}