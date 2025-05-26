package com.plazoleta.user_microservice.domain.validation;


import com.plazoleta.user_microservice.domain.exception.RoleNotAllowedException;
import com.plazoleta.user_microservice.domain.exception.UnderAgeOwnerException;
import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @InjectMocks
    private UserValidator userValidator;

    private final Role adminRole = new Role(1L,RoleList.ROLE_ADMIN,"Administrador");
    private final Role ownerRole = new Role(2L,RoleList.ROLE_OWNER,"Propietario");
    private final User sampleOwnerUser = User.builder()
            .id(2L)
            .firstName("First")
            .lastName("Last")
            .identityNumber(new IdentityNumber("1231321322"))
            .phoneNumber(new PhoneNumber("+573111551451"))
            .dateOfBirth(LocalDate.of(2007, 1, 2))
            .email(new Email("test@example.com"))
            .password("password")
            .role(ownerRole)
            .build();

    @Nested
    class ValidateUserCreationTest {

        @Test
        void shouldNotThrowExceptionWhenUserCreatedIsValid(){
            Role creator = adminRole;

            assertDoesNotThrow(() -> userValidator.validateUserCreation(sampleOwnerUser, creator));
        }

        @Test
        void shouldNotThrowExceptionWhenUserCreatedIsValidAndNotValidateAge(){
            User newUser = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(new Role(1L,RoleList.ROLE_EMPLOYED,"Empleado"))
                    .build();

            Role updater = ownerRole;

            assertDoesNotThrow(() -> userValidator.validateUserCreation(newUser, updater));
        }

        @Test
        void shouldThrowIfEmailAlreadyExists() {
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(sampleOwnerUser);

            User newUser = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role creator = adminRole;

            assertThrows(UserAlreadyExistsException.class, () -> userValidator.validateUserCreation(newUser, creator));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolAdmin() {
            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(new Role(4L,RoleList.ROLE_CUSTOMER,"Cliente"))
                    .build();
            Role updater = adminRole;

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolOwner() {
            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = ownerRole;

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolEmployed() {
            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = new Role(4L,RoleList.ROLE_EMPLOYED,"Empleado");

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolCustomer() {
            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = new Role(4L,RoleList.ROLE_CUSTOMER,"Cliente");

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(user, updater));
        }

        @Test
        void shouldThrowIfOwnerIsUnderAge() {
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);

            User newUser = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2008, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();

            Role creator = adminRole;

            assertThrows(UnderAgeOwnerException.class, () -> userValidator.validateUserCreation(newUser, creator));
        }

        @Test
        void shouldPassIfValid() {
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);

            assertDoesNotThrow(() -> userValidator.validateUserCreation(sampleOwnerUser, adminRole));
        }
    }

    @Nested
    class ValidateUserUpdateTest {

        @Test
        void shouldNotThrowExceptionWhenUserUpdateIsValid(){
            User userOld = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);
            when(userPersistencePort.getUser(2L)).thenReturn(userOld);

            assertDoesNotThrow(() -> userValidator.validateUserUpdate(sampleOwnerUser, adminRole));
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userPersistencePort.getUser(1L)).thenReturn(null);

            User user = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();

            Role updater = adminRole;

            assertThrows(UserNotFoundException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfEmailUsedByAnother() {

            User userOld = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();

            when(userPersistencePort.getUser(userOld.getId())).thenReturn(userOld);

            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(sampleOwnerUser);

            User user = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();

            Role updater = adminRole;

            assertThrows(UserAlreadyExistsException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignment() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);


            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = new Role(4L,RoleList.ROLE_EMPLOYED,"Empleado");

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolAdmin() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);


            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(new Role(4L,RoleList.ROLE_CUSTOMER,"Cliente"))
                    .build();
            Role updater = adminRole;

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolOwner() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);


            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = ownerRole;

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleAssignmentUpdaterRolCustomer() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);


            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = new Role(4L,RoleList.ROLE_CUSTOMER,"Cliente");

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfInvalidRoleOwnerAger() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);

            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2008, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = adminRole;

            assertThrows(UnderAgeOwnerException.class, () -> userValidator.validateUserUpdate(user, updater));
        }

        @Test
        void shouldThrowIfAgeOwnerIsNull() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);


            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(null)
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role updater = adminRole;

            assertThrows(UnderAgeOwnerException.class, () -> userValidator.validateUserUpdate(user, updater));
        }
    }

    @Nested
    class ValidateUserDeleteTest {

        @Test
        void shouldNotThrowExceptionWhenUserDeleteIsValid(){
            when(userPersistencePort.getUser(1L)).thenReturn(sampleOwnerUser);
            Role deleter = adminRole;

            assertDoesNotThrow(() -> userValidator.validateUserDelete(1L, deleter));
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userPersistencePort.getUser(1L)).thenReturn(null);
            Role deleter = adminRole;

            assertThrows(UserNotFoundException.class, () -> userValidator.validateUserDelete(1L, deleter));
        }

        @Test
        void shouldThrowIfDeleterLacksPermission() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);

            Role deleter = ownerRole;

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(2L, deleter));
        }

        @Test
        void shouldThrowIfDeleterRolNotAllowedAdmin() {
            User user = User.builder()
                    .id(2L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2007, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(adminRole)
                    .build();
            when(userPersistencePort.getUser(2L)).thenReturn(user);

            Role deleter = adminRole;

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(2L, deleter));
        }

        @Test
        void shouldThrowIfDeleterRolNotAllowedEmployed() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);

            Role deleter = new Role(1L,RoleList.ROLE_EMPLOYED,"Empleado");

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(2L, deleter));
        }

        @Test
        void shouldThrowIfDeleterRolNotAllowedCustomer() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);

            Role deleter = new Role(1L,RoleList.ROLE_CUSTOMER,"Cliente");

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(2L, deleter));
        }
    }
}
