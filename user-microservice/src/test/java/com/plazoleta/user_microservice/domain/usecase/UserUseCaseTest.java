package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort persistencePort;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserUseCase userUseCase;

    private final User sampleAdminUser = User.builder()
            .id(1L)
            .firstName("First")
            .lastName("Last")
            .identityNumber(new IdentityNumber("1231321322"))
            .phoneNumber(new PhoneNumber("+573111551451"))
            .email(new Email("admin@test.com"))
            .password("password")
            .role(new Role(1L, RoleList.ROLE_ADMIN,"Administrador"))
            .build();

    private final User sampleOwnerUser = User.builder()
            .id(2L)
            .firstName("First")
            .lastName("Last")
            .identityNumber(new IdentityNumber("1231321322"))
            .phoneNumber(new PhoneNumber("+573111551451"))
            .dateOfBirth(LocalDate.of(2007, 1, 2))
            .email(new Email("owner@test.com"))
            .password("password")
            .role(new Role(2L, RoleList.ROLE_OWNER,"Propietario"))
            .build();


    @Test
    void shouldSaveUserSuccessfullyWhenValidationPasses() {
        when(persistencePort.saveUser(sampleOwnerUser)).thenReturn(sampleOwnerUser);

        User user = userUseCase.saveUser(sampleOwnerUser, sampleAdminUser.getRole());

        assertEquals(sampleOwnerUser, user);
        verify(userValidator).validateUserCreation(sampleOwnerUser, sampleAdminUser.getRole());
        verify(persistencePort).saveUser(sampleOwnerUser);
    }

    @Test
    void shouldReturnUserById() {
        when(persistencePort.getUser(1L)).thenReturn(sampleAdminUser);

        User user = userUseCase.getUser(1L);

        assertEquals(sampleAdminUser, user);
    }

    @Test
    void shouldReturnUserByEmail() {
        when(persistencePort.getUserByEmail(new Email("admin@test.com"))).thenReturn(sampleAdminUser);

        User user = userUseCase.getUserByEmail(new Email("admin@test.com"));

        assertEquals(sampleAdminUser, user);
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(sampleAdminUser);
        when(persistencePort.getAllUsers()).thenReturn(users);

        List<User> result = userUseCase.getAllUsers();

        assertEquals(users, result);
    }

    @Test
    void shouldUpdateUser() {
        User updatedUser = User.builder()
                .id(2L)
                .firstName("First Name")
                .lastName("Last Name")
                .identityNumber(new IdentityNumber("1231328441322"))
                .phoneNumber(new PhoneNumber("+573111551451"))
                .dateOfBirth(LocalDate.of(2007, 3, 2))
                .email(new Email("owneras@test.com"))
                .password("password")
                .role(new Role(2L, RoleList.ROLE_OWNER,"Propietario"))
                .build();

        userUseCase.updateUser(updatedUser, sampleAdminUser.getRole());

        verify(userValidator).validateUserUpdate(updatedUser, sampleAdminUser.getRole());
        verify(persistencePort).updateUser(updatedUser);
    }

    @Test
    void ShouldDeleteUserSuccessful(){
        when(persistencePort.getUser(1L)).thenReturn(sampleAdminUser);
        userUseCase.deleteUser(1L);

        verify(persistencePort).deleteUser(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingUser(){
        when(persistencePort.getUser(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class,()->userUseCase.deleteUser(1L));
    }
}
