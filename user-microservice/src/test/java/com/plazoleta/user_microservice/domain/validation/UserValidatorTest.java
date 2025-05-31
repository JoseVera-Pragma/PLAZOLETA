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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @InjectMocks
    private UserValidator userValidator;

    private final Role adminRole = new Role(1L, RoleList.ROLE_ADMIN, "Administrador");
    private final Role ownerRole = new Role(2L, RoleList.ROLE_OWNER, "Propietario");
    private final Role employedRole = new Role(4L, RoleList.ROLE_EMPLOYED, "Empleado");
    private final Role customerRole = new Role(3L, RoleList.ROLE_CUSTOMER, "Cliente");
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

    private User customUser(Role role) {
        return User.builder()
                .id(2L)
                .firstName("First")
                .lastName("Last")
                .identityNumber(new IdentityNumber("1231321322"))
                .phoneNumber(new PhoneNumber("+573111551451"))
                .dateOfBirth(LocalDate.of(2007, 1, 2))
                .email(new Email("test@example.com"))
                .password("password")
                .role(role)
                .build();
    }

    @Nested
    class ValidateUserCreationTest {

        @Test
        void shouldNotThrowExceptionWhenUserCreatedIsValid() {
            Role creator = adminRole;

            assertDoesNotThrow(() -> userValidator.validateUserCreation(sampleOwnerUser, creator));
        }

        @Test
        void shouldNotThrowExceptionWhenUserCreatedIsValidAndNotValidateAge() {
            User newUser = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .email(new Email("test@example.com"))
                    .password("password")
                    .role(employedRole)
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

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(user, employedRole));
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

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(user, customerRole));
        }

        @Test
        void shouldPassIfValid() {
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);

            assertDoesNotThrow(() -> userValidator.validateUserCreation(sampleOwnerUser, adminRole));
        }

        @Test
        void shouldThrowIfEmailAlreadyExistsEvenWithDifferentCase() {
            when(userPersistencePort.getUserByEmail(new Email("test@example.com"))).thenReturn(sampleOwnerUser);

            User newUser = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .email(new Email("TEST@EXAMPLE.COM"))
                    .password("password")
                    .role(ownerRole)
                    .build();
            Role creator = adminRole;

            assertThrows(UserAlreadyExistsException.class, () -> userValidator.validateUserCreation(newUser, creator));
        }

        @Test
        void shouldNotThrowWhenOwnerIsExactly18YearsOld() {
            LocalDate today = LocalDate.now();
            LocalDate birthday = today.minusYears(18);

            User user = User.builder()
                    .id(1L)
                    .firstName("First")
                    .lastName("Last")
                    .identityNumber(new IdentityNumber("1231321322"))
                    .phoneNumber(new PhoneNumber("+573111551451"))
                    .dateOfBirth(birthday)
                    .email(new Email("test18@example.com"))
                    .password("password")
                    .role(ownerRole)
                    .build();

            when(userPersistencePort.getUserByEmail(user.getEmail())).thenReturn(null);

            assertDoesNotThrow(() -> userValidator.validateUserCreation(user, adminRole));
        }

        @Test
        void shouldThrowWhenOwnerTriesToCreateAdminUser() {
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(customUser(adminRole), ownerRole));
        }

        @Test
        void shouldThrowWhenOwnerTriesToCreateOwnerUser() {
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(customUser(ownerRole), ownerRole));
        }

        @Test
        void shouldThrowWhenEmployedTriesToCreateEmployedUser() {
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserCreation(customUser(employedRole), employedRole));
        }
    }

    @Nested
    class ValidateUserUpdateTest {

        @Test
        void shouldNotThrowExceptionWhenUserUpdateIsValid() {
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

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(user, employedRole));
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

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(user, customerRole));
        }

        @Test
        void shouldThrowWhenOwnerTriesToUpdateAdminUser() {
            when(userPersistencePort.getUser(2L)).thenReturn(customUser(adminRole));
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(customUser(adminRole), ownerRole));
        }

        @Test
        void shouldThrowWhenOwnerTriesToUpdateOwnerUser() {
            when(userPersistencePort.getUser(2L)).thenReturn(customUser(ownerRole));
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(customUser(ownerRole), ownerRole));
        }

        @Test
        void shouldThrowWhenEmployedTriesToUpdateEmployedUser() {
            when(userPersistencePort.getUser(2L)).thenReturn(customUser(employedRole));
            when(userPersistencePort.getUserByEmail(sampleOwnerUser.getEmail())).thenReturn(null);
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(customUser(employedRole), employedRole));
        }
    }

    @Nested
    class ValidateUserDeleteTest {

        @Test
        void shouldNotThrowExceptionWhenUserDeleteIsValid() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);
            assertDoesNotThrow(() -> userValidator.validateUserDelete(sampleOwnerUser, adminRole));
        }

        @Test
        void shouldThrowIfUserNotFound() {
            assertThrows(UserNotFoundException.class, () -> userValidator.validateUserDelete(null, adminRole));
        }

        @Test
        void shouldThrowIfDeleterLacksPermission() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(sampleOwnerUser, ownerRole));
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

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(user, deleter));
        }

        @Test
        void shouldThrowIfDeleterRolNotAllowedEmployed() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(sampleOwnerUser, employedRole));
        }

        @Test
        void shouldThrowIfDeleterRolNotAllowedCustomer() {
            when(userPersistencePort.getUser(2L)).thenReturn(sampleOwnerUser);

            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserDelete(sampleOwnerUser, customerRole));
        }


        @Test
        void shouldThrowWhenOwnerTriesToDeleteAdminUser() {
            when(userPersistencePort.getUser(2L)).thenReturn(customUser(adminRole));
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(customUser(adminRole), ownerRole));
        }

        @Test
        void shouldThrowWhenOwnerTriesToDeleteOwnerUser() {
            when(userPersistencePort.getUser(2L)).thenReturn(customUser(ownerRole));
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(customUser(ownerRole), ownerRole));
        }

        @Test
        void shouldThrowWhenEmployedTriesToDeleteEmployedUser() {
            when(userPersistencePort.getUser(2L)).thenReturn(customUser(employedRole));
            assertThrows(RoleNotAllowedException.class, () -> userValidator.validateUserUpdate(customUser(employedRole), employedRole));
        }
    }
}
