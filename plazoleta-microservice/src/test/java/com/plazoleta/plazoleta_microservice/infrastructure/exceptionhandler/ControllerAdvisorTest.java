package com.plazoleta.plazoleta_microservice.infrastructure.exceptionhandler;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.UnauthorizedOwnerException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.CustomerHasActiveOrderException;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.DishesNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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
        ResponseEntity<ApiError> response = advisor.handleBadRequestDomainExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid NIT", response.getBody().getMessage());
    }

    @Test
    void handleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<ApiError> response = advisor.handleNotFoundDomainExceptions(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleDuplicateNit() {
        DuplicateNitException ex = new DuplicateNitException("Duplicate NIT");
        ResponseEntity<ApiError> response = advisor.handleDuplicateDomainExceptions(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate NIT", response.getBody().getMessage());
    }

    @Test
    void handleValidation() {
        BindingResult result = mock(BindingResult.class);
        when(result.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "must not be null")));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, result);
        ResponseEntity<ApiError> response = advisor.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Integrity error");
        ResponseEntity<ApiError> response = advisor.handleDataIntegrityViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data integrity violation", response.getBody().getMessage());
    }

    @Test
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("val", String.class, "param", null, new IllegalArgumentException("Invalid"));
        ResponseEntity<ApiError> response = advisor.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Invalid value"));
    }

    @Test
    void handleGeneral() {
        Exception ex = new Exception("Something went wrong");
        ResponseEntity<ApiError> response = advisor.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
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

    @Test
    void handleUnauthorizedDomainExceptions_shouldReturn401() {
        RuntimeException ex = new UnauthorizedOwnerException("No tienes permiso");
        ResponseEntity<ApiError> response = advisor.handleUnauthorizedDomainExceptions(ex, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No tienes permiso", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleMissingParam_shouldReturn400() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "String");
        ResponseEntity<ApiError> response = advisor.handleMissingParam(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Missing parameter: id", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleDishesNotFound_shouldReturn404() {
        RuntimeException ex = new DishesNotFoundException("Platos no encontrados");
        ResponseEntity<ApiError> response = advisor.handleDishesNotFound(ex, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Platos no encontrados", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleCustomerHasActiveOrderException_shouldReturn409WithMessage() {
        CustomerHasActiveOrderException ex = new CustomerHasActiveOrderException(1L);
        ResponseEntity<String> response = advisor.handleCustomerHasActiveOrderException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Customer with ID 1 already has an order in process.", response.getBody());
    }

    @Test
    void handleUsernameNotFound_shouldReturn404() {
        UsernameNotFoundException ex = new UsernameNotFoundException("Usuario no encontrado");
        ResponseEntity<ApiError> response = advisor.handleUsernameNotFound(ex, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario no encontrado", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleUnauthorized_fromHttpClient_shouldReturn403() {
        HttpClientErrorException.Unauthorized ex = mock(HttpClientErrorException.Unauthorized.class);
        when(ex.getMessage()).thenReturn("Token inválido");

        ResponseEntity<ApiError> response = advisor.handleUnauthorized(ex, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Token inválido", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleAuthentication_shouldReturn401() {
        AuthenticationException ex = new BadCredentialsException("Token expirado");
        ResponseEntity<ApiError> response = advisor.handleAuthentication(ex, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Token expirado", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleBadCredentials_shouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Credenciales inválidas");
        ResponseEntity<ApiError> response = advisor.handleBadCredentials(ex, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        AccessDeniedException ex = new AccessDeniedException("Acceso restringido");
        ResponseEntity<ApiError> response = advisor.handleAccessDenied(ex, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acceso restringido", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
    }
}