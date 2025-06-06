package com.plazoleta.plazoleta_microservice.infrastructure.exceptionhandler;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.UnauthorizedOwnerException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidPhoneNumberException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.CustomerHasActiveOrderException;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.UserServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerAdvisorTest {

    private ControllerAdvisor advisor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgNotValidEx;

    @Mock
    private ConstraintViolationException constraintViolationEx;

    @Mock
    private ConstraintViolation<?> violation;

    @Mock
    private Path path;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        advisor = new ControllerAdvisor();
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void testHandleBadRequestDomainExceptions() {
        InvalidPhoneNumberException ex = new InvalidPhoneNumberException("Invalid phone");
        ResponseEntity<ApiError> response = advisor.handleBadRequestDomainExceptions(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid phone", response.getBody().getMessage());
    }

    @Test
    void testHandleUnauthorizedDomainExceptions() {
        UnauthorizedOwnerException ex = new UnauthorizedOwnerException("Unauthorized owner");
        ResponseEntity<ApiError> response = advisor.handleUnauthorizedDomainExceptions(ex, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized owner", response.getBody().getMessage());
    }

    @Test
    void testHandleMissingParam() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "type");
        when(request.getRequestURI()).thenReturn("/test-missing-param");
        ResponseEntity<ApiError> response = advisor.handleMissingParam(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Missing parameter: param"));
    }

    @Test
    void testHandleNotFoundDomainExceptions() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<ApiError> response = advisor.handleNotFoundDomainExceptions(ex, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testHandleCustomerHasActiveOrderException() {
        CustomerHasActiveOrderException ex = new CustomerHasActiveOrderException(1L);
        ResponseEntity<String> response = advisor.handleCustomerHasActiveOrderException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Customer with ID " + 1L + " already has an order in process.", response.getBody());
    }

    @Test
    void testHandleDuplicateDomainExceptions() {
        DuplicateNitException ex = new DuplicateNitException("Duplicate NIT");
        ResponseEntity<ApiError> response = advisor.handleDuplicateDomainExceptions(ex, request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate NIT", response.getBody().getMessage());
    }

    @Test
    void testHandleValidation() {
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("object", "field", "error message");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        when(methodArgNotValidEx.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiError> response = advisor.handleValidation(methodArgNotValidEx, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
        assertTrue(response.getBody().getMessage().contains("field=error message"));
    }

    @Test
    void testHandleConstraintViolation() {
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("field");
        when(violation.getMessage()).thenReturn("message");
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        when(constraintViolationEx.getConstraintViolations()).thenReturn((Set) violations);

        ResponseEntity<ApiError> response = advisor.handleConstraintViolation(constraintViolationEx, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
        assertTrue(response.getBody().getMessage().contains("field=message"));
    }

    @Test
    void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Integrity error");
        ResponseEntity<ApiError> response = advisor.handleDataIntegrityViolation(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Data integrity violation", response.getBody().getMessage());
    }

    @Test
    void testHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("abc", String.class, "param", null, new Throwable());
        ResponseEntity<ApiError> response = advisor.handleTypeMismatch(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Invalid value 'abc' for parameter 'param'"));
    }

    @Test
    void testHandleUsernameNotFound() {
        UsernameNotFoundException ex = new UsernameNotFoundException("Username not found");
        ResponseEntity<ApiError> response = advisor.handleUsernameNotFound(ex, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Username not found", response.getBody().getMessage());
    }

    @Test
    void testHandleUnauthorized() {
        HttpClientErrorException.Unauthorized ex = mock(HttpClientErrorException.Unauthorized.class);
        when(ex.getMessage()).thenReturn("Unauthorized error");
        ResponseEntity<ApiError> response = advisor.handleUnauthorized(ex, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Unauthorized error", response.getBody().getMessage());
    }

    @Test
    void testHandleAuthentication() {
        AuthenticationException ex = mock(AuthenticationException.class);
        when(ex.getMessage()).thenReturn("Authentication failed");
        ResponseEntity<ApiError> response = advisor.handleAuthentication(ex, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication failed", response.getBody().getMessage());
    }

    @Test
    void testHandleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ResponseEntity<ApiError> response = advisor.handleBadCredentials(ex, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody().getMessage());
    }

    @Test
    void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ResponseEntity<ApiError> response = advisor.handleAccessDenied(ex, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void testHandleUserServiceUnavailable() {
        UserServiceUnavailableException ex = new UserServiceUnavailableException("Service unavailable", new Throwable("sd"));
        ResponseEntity<ApiError> response = advisor.handleUserServiceUnavailable(ex, request);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service unavailable", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneral() {
        Exception ex = new Exception("General error");
        ResponseEntity<ApiError> response = advisor.handleGeneral(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Unexpected error: General error"));
    }
}