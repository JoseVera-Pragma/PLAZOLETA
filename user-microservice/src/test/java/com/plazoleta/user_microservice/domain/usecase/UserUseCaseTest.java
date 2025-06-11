package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;
    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;
    @Mock
    private IPasswordEncoderPort passwordEncoder;
    @Mock
    private IRolePersistencePort rolePersistencePort;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserUseCase userUseCase;

    private User newUser;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        newUser = new User.Builder()
                .firstName("Jose")
                .lastName("Vera")
                .email("jose@email.com")
                .identityNumber("123456")
                .password("1234")
                .phoneNumber("+573001234567")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        customerRole = new Role(1L, RoleList.ROLE_CUSTOMER,"ROLE_CUSTOMER");
    }

    @Test
    void saveCustomerUser_success() {
        when(authenticatedUserPort.getCurrentUserRole()).thenReturn(new Role(2L, RoleList.ROLE_ADMIN,"ROLE_ADMIN"));
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        userUseCase.saveCustomerUser(newUser);

        verify(userValidator).validateUserCreation(any(User.class), any(Role.class));
    }

    @Test
    void validateRequiredFields_success() {
        assertDoesNotThrow(() -> userValidator.validateRequiredFields(newUser));
    }

    @Test
    void saveEmployedUser_success() {
        when(authenticatedUserPort.getCurrentUserRole()).thenReturn(new Role(2L, RoleList.ROLE_OWNER,"ROLE_OWNER"));
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(Optional.of(new Role(1L, RoleList.ROLE_EMPLOYED,"ROLE_EMPLOYED")));
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        userUseCase.saveEmployedUser(newUser);

        verify(userValidator).validateUserCreation(any(User.class), any(Role.class));
    }

    @Test
    void saveOwnerUser_success() {
        when(authenticatedUserPort.getCurrentUserRole()).thenReturn(new Role(2L, RoleList.ROLE_ADMIN,"ROLE_ADMIN"));
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(Optional.of(new Role(1L, RoleList.ROLE_OWNER,"ROLE_OWNER")));
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        userUseCase.saveOwnerUser(newUser);

        verify(userValidator).validateUserCreation(any(User.class), any(Role.class));
    }

    @Test
    void saveCustomerUser_roleNotFound_throwsException() {
        when(authenticatedUserPort.getCurrentUserRole()).thenReturn(new Role(2L, RoleList.ROLE_ADMIN,"ROLE_ADMIN"));
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> userUseCase.saveCustomerUser(newUser));
    }

    @Test
    void getUser_success() {
        when(userPersistencePort.getUser(1L)).thenReturn(Optional.of(newUser));
        User result = userUseCase.getUser(1L);
        assertEquals(newUser, result);
    }

    @Test
    void getUser_notFound_throwsException() {
        when(userPersistencePort.getUser(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userUseCase.getUser(1L));
    }

    @Test
    void deleteUser_success() {
        when(userPersistencePort.getUser(1L)).thenReturn(Optional.of(newUser));
        userUseCase.deleteUser(1L);
        verify(userPersistencePort).deleteUser(1L);
    }

    @Test
    void deleteUser_notFound_throwsException() {
        when(userPersistencePort.getUser(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userUseCase.deleteUser(1L));
    }

    @Test
    void updateUser_delegatesCorrectly() {
        when(userPersistencePort.getUser(1L)).thenReturn(Optional.of(newUser));
        userUseCase.updateUser(1L, newUser);
        User userExpected = newUser.withId(1L);
        verify(userPersistencePort).saveUser(userExpected);
    }

    @Test
    void updateUser_notFound_throwsException() {
        when(userPersistencePort.getUser(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userUseCase.updateUser(1L,newUser));
    }

    @Test
    void getUserByEmail_success() {
        when(userPersistencePort.getUserByEmail("jose@email.com")).thenReturn(Optional.of(newUser));
        User result = userUseCase.getUserByEmail("jose@email.com");
        assertEquals(newUser, result);
    }

    @Test
    void getUserByEmail_notFound() {
        when(userPersistencePort.getUserByEmail("jose@email.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userUseCase.getUserByEmail("jose@email.com"));
    }

    @Test
    void getAllUsers_returnsList() {
        List<User> users = List.of(newUser);
        when(userPersistencePort.getAllUsers()).thenReturn(users);
        List<User> result = userUseCase.getAllUsers();
        assertEquals(1, result.size());
    }
}
