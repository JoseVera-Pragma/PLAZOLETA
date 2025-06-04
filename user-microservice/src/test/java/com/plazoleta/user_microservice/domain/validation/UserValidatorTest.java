package com.plazoleta.user_microservice.domain.validation;


import com.plazoleta.user_microservice.domain.exception.RoleNotAllowedException;
import com.plazoleta.user_microservice.domain.exception.UnderAgeException;
import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserValidatorTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    private UserValidator userValidator;


    private final Role adminRole = new Role(1L, RoleList.ROLE_ADMIN,"ROLE_ADMIN");
    private final Role ownerRole = new Role(2L, RoleList.ROLE_OWNER,"ROLE_OWNER");
    private final Role employedRole = new Role(3L, RoleList.ROLE_EMPLOYED,"ROLE_EMPLOYED");
    private final Role customerRole = new Role(4L, RoleList.ROLE_CUSTOMER,"ROLE_CUSTOMER");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userValidator = new UserValidator(userPersistencePort);
    }

    private User.Builder baseUserBuilder() {
        return new User.Builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .identityNumber("123456789")
                .phoneNumber("+1234567890")
                .email("john@example.com")
                .password("password")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .role(employedRole)
                .restaurantId(10L);
    }

    @Test
    void validateUserCreation_validData_noExceptionThrown() {
        User validUser = new User.Builder()
                .id(1L)
                .firstName("Carlos")
                .lastName("Pérez")
                .identityNumber("123456789")
                .phoneNumber("+573001112233")
                .email("carlos.perez@example.com")
                .password("securePassword123")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .role(employedRole)
                .restaurantId(100L)
                .build();

        when(userPersistencePort.getUserByEmail(validUser.getEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userValidator.validateUserCreation(validUser, ownerRole));
    }

    @Test
    void validateEmail_validEmail_noException() {
        assertDoesNotThrow(() -> userValidator.validateEmail("test@example.com"));
    }

    @Test
    void validateEmail_invalidEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateEmail("invalidemail"));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateEmail(""));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateEmail(null));
    }

    @Test
    void validateIdentityNumber_valid_noException() {
        assertDoesNotThrow(() -> userValidator.validateIdentityNumber("123456"));
    }

    @Test
    void validateIdentityNumber_invalid_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateIdentityNumber("abc123"));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateIdentityNumber(""));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateIdentityNumber(null));
    }

    @Test
    void validatePhoneNumber_valid_noException() {
        assertDoesNotThrow(() -> userValidator.validatePhoneNumber("+1234567890"));
        assertDoesNotThrow(() -> userValidator.validatePhoneNumber("1234567890"));
    }

    @Test
    void validatePhoneNumber_invalid_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> userValidator.validatePhoneNumber("abc123"));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validatePhoneNumber(""));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validatePhoneNumber(null));
    }

    @Test
    void validateUniqueEmail_emailNotExists_noException() {
        when(userPersistencePort.getUserByEmail("new@example.com")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userValidator.validateUniqueEmail("new@example.com"));
    }

    @Test
    void validateUniqueEmail_emailExists_throwsUserAlreadyExists() {
        when(userPersistencePort.getUserByEmail("exist@example.com")).thenReturn(Optional.of(mock(User.class)));
        assertThrows(UserAlreadyExistsException.class, () -> userValidator.validateUniqueEmail("exist@example.com"));
    }

    @Test
    void validateOwnerAge_ownerAbove18_noException() {
        User user = baseUserBuilder()
                .role(ownerRole)
                .dateOfBirth(LocalDate.now().minusYears(19))
                .build();

        assertDoesNotThrow(() -> userValidator.validateOwnerAge(user));
    }

    @Test
    void validateOwnerAge_ownerBelow18_throwsUnderAge() {
        User user = baseUserBuilder()
                .role(ownerRole)
                .dateOfBirth(LocalDate.now().minusYears(17))
                .build();

        UnderAgeException ex = assertThrows(UnderAgeException.class, () -> userValidator.validateOwnerAge(user));
        assertEquals("Owner must be at least 18 years old.", ex.getMessage());
    }

    @Test
    void validateOwnerAge_ownerNoDob_throwsUnderAge() {
        User user = baseUserBuilder()
                .role(ownerRole)
                .dateOfBirth(null)
                .build();

        UnderAgeException ex = assertThrows(UnderAgeException.class, () -> userValidator.validateOwnerAge(user));
        assertEquals("Date of birth is required.", ex.getMessage());
    }

    @Test
    void validateRestaurantId_employedWithRestaurantId_noException() {
        User user = baseUserBuilder()
                .role(employedRole)
                .restaurantId(5L)
                .build();

        assertDoesNotThrow(() -> userValidator.validateRestaurantId(user));
    }

    @Test
    void validateRestaurantId_employedWithoutRestaurantId_throwsException() {
        User user = baseUserBuilder()
                .role(employedRole)
                .restaurantId(null)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userValidator.validateRestaurantId(user));
        assertEquals("An employed user must be associated with a restaurant ID.", ex.getMessage());
    }

    @Test
    void validateRestaurantId_nonEmployed_noException() {
        User user = baseUserBuilder()
                .role(ownerRole)
                .restaurantId(null)
                .build();

        assertDoesNotThrow(() -> userValidator.validateRestaurantId(user));
    }

    @Test
    void validateRoleCreationPermissions_ownerByAdmin_noException() {
        userValidator.validateRoleCreationPermissions(ownerRole, adminRole);
    }

    @Test
    void validateRoleCreationPermissions_ownerByNonAdmin_throwsException() {
        RoleNotAllowedException ex = assertThrows(RoleNotAllowedException.class,
                () -> userValidator.validateRoleCreationPermissions(ownerRole, employedRole));
        assertEquals("Only ADMIN can create an OWNER.", ex.getMessage());
    }

    @Test
    void validateRoleCreationPermissions_employedByOwner_noException() {
        userValidator.validateRoleCreationPermissions(employedRole, ownerRole);
    }

    @Test
    void validateRoleCreationPermissions_employedByNonOwner_throwsException() {
        RoleNotAllowedException ex = assertThrows(RoleNotAllowedException.class,
                () -> userValidator.validateRoleCreationPermissions(employedRole, adminRole));
        assertEquals("Only OWNER can create an EMPLOYED.", ex.getMessage());
    }

    @Test
    void validateRoleCreationPermissions_adminByAdmin_noException() {
        userValidator.validateRoleCreationPermissions(adminRole, adminRole);
    }

    @Test
    void validateRoleCreationPermissions_adminByNonAdmin_throwsException() {
        RoleNotAllowedException ex = assertThrows(RoleNotAllowedException.class,
                () -> userValidator.validateRoleCreationPermissions(adminRole, ownerRole));
        assertEquals("Creation of ADMIN users is not allowed.", ex.getMessage());
    }
/*
    @Test
    void validateUserCreation_allValid_noException() {
        User newUser = baseUserBuilder()
                .role(employedRole)
                .restaurantId(1L)
                .build();

        when(userPersistencePort.getUserByEmail(newUser.getEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userValidator.validateUserCreation(newUser, adminRole));
    }*/
}