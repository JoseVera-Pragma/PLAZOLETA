package com.plazoleta.user_microservice.infrastructure.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.plazoleta.user_microservice.domain.exception.*;
import com.plazoleta.user_microservice.infrastructure.exception.NotDataFoundException;
import com.plazoleta.user_microservice.infrastructure.exception.RoleAssignedException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerAdvisorTest {

    @InjectMocks
    private ControllerAdvisor controllerAdvisor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    private static final String TEST_URI = "/test/uri";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Test
    void handleRoleAlreadyExists_ShouldReturnConflictResponse() {
        String errorMessage = "Role already exists";
        RoleAlreadyExistsException exception = new RoleAlreadyExistsException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUserAlreadyExists(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleRoleInUse_ShouldReturnConflictResponse() {
        String errorMessage = "Role is in use";
        RoleInUseException exception = new RoleInUseException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleRoleInUse(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleRoleNotAllowed_ShouldReturnForbiddenResponse() {
        String errorMessage = "Role not allowed";
        RoleNotAllowedException exception = new RoleNotAllowedException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleRoleNotAllowed(exception, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void handleRoleNotFound_ShouldReturnNotFoundResponse() {
        String errorMessage = "Role not found";
        RoleNotFoundException exception = new RoleNotFoundException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleRoleNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleRoleAssigned_ShouldReturnConflictResponseWithCustomMessage() {
        RoleAssignedException exception = new RoleAssignedException();

        ResponseEntity<ApiError> response = controllerAdvisor.handleRoleAssigned(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot delete: Role is assigned to one or more users.", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleUnderAgeOwner_ShouldReturnConflictResponse() {
        String errorMessage = "Owner must be of legal age";
        UnderAgeOwnerException exception = new UnderAgeOwnerException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUnderAgeOwner(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleNoDataFound_ShouldReturnNotFoundResponse() {
        String errorMessage = "No data found";
        NotDataFoundException exception = new NotDataFoundException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleNoDataFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleUserNotFound_ShouldReturnNotFoundResponse() {
        String errorMessage = "User not found";
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUserNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleUserAlreadyExists_ShouldReturnConflictResponse() {
        String errorMessage = "User already exists";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUserAlreadyExists(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleValidation_ShouldReturnBadRequestWithValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError1 = new FieldError("object", "field1", "Field1 is required");
        FieldError fieldError2 = new FieldError("object", "field2", "Field2 must be valid");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<ApiError> response = controllerAdvisor.handleValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
        assertTrue(response.getBody().getMessage().contains("field1"));
        assertTrue(response.getBody().getMessage().contains("field2"));
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleUnreadable_WithEnumException_ShouldReturnBadRequestWithEnumValues() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);

        when(exception.getCause()).thenReturn(invalidFormatException);
        when(invalidFormatException.getTargetType()).thenReturn((Class) TestEnum.class);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUnreadable(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Invalid value for field"));
        assertTrue(response.getBody().getMessage().contains("VALUE1"));
        assertTrue(response.getBody().getMessage().contains("VALUE2"));
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    void handleUnreadable_WithLocalDateException_ShouldReturnBadRequestWithDateFormat() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);

        when(exception.getCause()).thenReturn(invalidFormatException);
        when(invalidFormatException.getTargetType()).thenReturn((Class) LocalDate.class);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUnreadable(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format. Expected format: yyyy-MM-dd", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    void handleUnreadable_WithDateTimeParseException_ShouldReturnBadRequestWithDateFormat() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        DateTimeParseException dateTimeParseException = mock(DateTimeParseException.class);

        when(exception.getCause()).thenReturn(dateTimeParseException);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUnreadable(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format. Expected format: yyyy-MM-dd", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    void handleUnreadable_WithGenericException_ShouldReturnBadRequestWithGenericMessage() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(new RuntimeException("Generic error"));

        ResponseEntity<ApiError> response = controllerAdvisor.handleUnreadable(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed JSON or incorrect data format", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturnBadRequestResponse() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<ApiError> response = controllerAdvisor.handleDataIntegrityViolation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Data integrity violation", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleTypeMismatch_ShouldReturnBadRequestWithParameterInfo() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("invalidValue");
        when(exception.getName()).thenReturn("parameterName");

        ResponseEntity<ApiError> response = controllerAdvisor.handleTypeMismatch(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value 'invalidValue' for parameter 'parameterName'", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleIllegalArgument_WithEnumMessage_ShouldReturnBadRequestWithRoleMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("No enum constant com.example.Role.INVALID");

        ResponseEntity<ApiError> response = controllerAdvisor.handleIllegalArgument(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid role. Please provide a valid role name.", response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleIllegalArgument_WithGenericMessage_ShouldReturnBadRequestWithOriginalMessage() {
        String errorMessage = "Generic illegal argument";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleIllegalArgument(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleUsernameNotFound_ShouldReturnNotFoundResponse() {
        String errorMessage = "Username not found";
        UsernameNotFoundException exception = new UsernameNotFoundException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleUsernameNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleUnauthorized_ShouldReturnForbiddenResponse() {
        // Given
        HttpClientErrorException.Unauthorized exception = mock(HttpClientErrorException.Unauthorized.class);
        String errorMessage = "Unauthorized access";
        when(exception.getMessage()).thenReturn(errorMessage);

        // When
        ResponseEntity<ApiError> response = controllerAdvisor.handleUnauthorized(exception, request);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void handleAuthentication_ShouldReturnUnauthorizedResponse() {
        String errorMessage = "Authentication failed";
        AuthenticationException exception = new AuthenticationException(errorMessage) {};

        ResponseEntity<ApiError> response = controllerAdvisor.handleAuthentication(exception, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void handleBadCredentials_ShouldReturnUnauthorizedResponse() {
        String errorMessage = "Bad credentials";
        BadCredentialsException exception = new BadCredentialsException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleBadCredentials(exception, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void handleAccessDenied_ShouldReturnForbiddenResponse() {
        String errorMessage = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleAccessDenied(exception, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void handleGeneral_ShouldReturnInternalServerErrorResponse() {
        String errorMessage = "Generic error";
        Exception exception = new Exception(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleGeneral(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: " + errorMessage, response.getBody().getMessage());
        assertEquals(TEST_URI, response.getBody().getPath());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void buildResponse_ShouldCreateCorrectApiError() {
        String errorMessage = "Test error";
        RoleNotFoundException exception = new RoleNotFoundException(errorMessage);

        ResponseEntity<ApiError> response = controllerAdvisor.handleRoleNotFound(exception, request);

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertNotNull(apiError.getTimestamp());
        assertEquals(404, apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
        assertEquals(errorMessage, apiError.getMessage());
        assertEquals(TEST_URI, apiError.getPath());
    }

    enum TestEnum {
        VALUE1, VALUE2
    }
}