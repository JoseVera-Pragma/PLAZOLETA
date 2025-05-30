package com.plazoleta.user_microservice.infrastructure.configuration;

import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    private Role adminRole;
    private Role ownerRole;
    private Role employedRole;
    private Role customerRole;
    private User adminUser;

    @BeforeEach
    void setUp() {
        adminRole = new Role(1L, RoleList.ROLE_ADMIN, "Administrador del sistema");
        ownerRole = new Role(2L, RoleList.ROLE_OWNER, "Propietario de restaurante");
        employedRole = new Role(3L, RoleList.ROLE_EMPLOYED, "Empleado de restaurante");
        customerRole = new Role(4L, RoleList.ROLE_CUSTOMER, "Cliente");

        adminUser = User.builder()
                .id(1L)
                .firstName("Super")
                .lastName("Admin")
                .identityNumber(new IdentityNumber("999999999"))
                .phoneNumber(new PhoneNumber("+573000000000"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email(new Email("admin@plazoleta.com"))
                .password("encodedPassword123")
                .role(adminRole)
                .build();
    }

    @Nested
    class RunMethodTests {

        @Test
        void shouldHandleInitializationWhenRolesAlreadyExist() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(adminUser);

            assertDoesNotThrow(() -> dataInitializer.run());

            verify(rolePersistencePort, never()).saveRole(any(Role.class));
            verify(userPersistencePort, never()).saveUser(any(User.class));
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        void shouldThrowRuntimeExceptionWhenInitializationFails() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN))
                    .thenThrow(new RuntimeException("Database connection error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dataInitializer.run());

            assertEquals("Fallo en la inicialización de datos", exception.getMessage());
            assertNotNull(exception.getCause());
            assertEquals("Database connection error", exception.getCause().getMessage());
        }
    }

    @Nested
    class RoleInitializationTests {

        @Test
        void shouldSkipRoleCreationWhenAdminRoleAlreadyExists() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(adminUser);

            dataInitializer.run();

            verify(rolePersistencePort, times(1)).getRoleByName(RoleList.ROLE_ADMIN);
            verify(rolePersistencePort, never()).saveRole(any(Role.class));
        }

        @Test
        void shouldCreateAllDefaultRolesWhenNoneExist() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(null);
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(null);
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(null);
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(null);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(adminUser);

            dataInitializer.run();

            verify(rolePersistencePort).saveRole(argThat(role ->
                    role.getName() == RoleList.ROLE_ADMIN &&
                            role.getDescription().equals("Administrador del sistema")));
            verify(rolePersistencePort).saveRole(argThat(role ->
                    role.getName() == RoleList.ROLE_OWNER &&
                            role.getDescription().equals("Propietario de restaurante")));
            verify(rolePersistencePort).saveRole(argThat(role ->
                    role.getName() == RoleList.ROLE_EMPLOYED &&
                            role.getDescription().equals("Empleado de restaurante")));
            verify(rolePersistencePort).saveRole(argThat(role ->
                    role.getName() == RoleList.ROLE_CUSTOMER &&
                            role.getDescription().equals("Cliente")));
        }

        @Test
        void shouldNotCreateRoleWhenItAlreadyExists() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(null);
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(null);
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(null);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(adminUser);

            dataInitializer.run();

            verify(rolePersistencePort, times(3)).saveRole(any(Role.class));
        }
    }

    @Nested
    class AdminUserInitializationTests {

        @Test
        void shouldCreateAdminUserWhenNoneExists() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(null);
            when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword123");

            dataInitializer.run();

            verify(passwordEncoder).encode("admin123");
            verify(userPersistencePort).saveUser(argThat(user ->
                    user.getFirstName().equals("Super") &&
                            user.getLastName().equals("Admin") &&
                            user.getIdentityNumber().getValue().equals("999999999") &&
                            user.getPhoneNumber().getValue().equals("+573000000000") &&
                            user.getDateOfBirth().equals(LocalDate.of(1990, 1, 1)) &&
                            user.getEmail().getValue().equals("admin@plazoleta.com") &&
                            user.getPassword().equals("encodedPassword123") &&
                            user.getRole().equals(adminRole)));
        }

        @Test
        void shouldNotCreateAdminUserWhenAlreadyExists() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(adminUser);

            dataInitializer.run();

            verify(passwordEncoder, never()).encode(anyString());
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        void shouldThrowRuntimeExceptionWhenAdminRoleNotFoundForUserCreation() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole).thenReturn(null);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dataInitializer.run());

            assertEquals("Fallo en la inicialización de datos", exception.getMessage());
            assertTrue(exception.getCause().getMessage().contains("Role ADMIN not found"));
        }

        @Test
        void shouldVerifyAdminUserEmailIsCheckedCorrectly() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(adminUser);

            dataInitializer.run();

            verify(userPersistencePort).getUserByEmail(argThat(email ->
                    email.getValue().equals("admin@plazoleta.com")));
        }
    }

    @Nested
    class ConstructorTests {

        @Test
        void shouldCreateDataInitializerWithAllRequiredDependencies() {
            DataInitializer initializer = new DataInitializer(
                    rolePersistencePort,
                    userPersistencePort,
                    passwordEncoder
            );

            assertNotNull(initializer);
        }
    }

    @Nested
    class ErrorHandlingTests {

        @Test
        void shouldPropagateExceptionWhenRolePersistenceFails() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(null);
            when(rolePersistencePort.saveRole(any(Role.class)))
                    .thenThrow(new RuntimeException("Database error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dataInitializer.run());

            assertEquals("Fallo en la inicialización de datos", exception.getMessage());
            assertEquals("Database error", exception.getCause().getMessage());
        }

        @Test
        void shouldPropagateExceptionWhenUserPersistenceFails() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(null);
            when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword123");
            when(userPersistencePort.saveUser(any(User.class)))
                    .thenThrow(new RuntimeException("User creation failed"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dataInitializer.run());

            assertEquals("Fallo en la inicialización de datos", exception.getMessage());
            assertEquals("User creation failed", exception.getCause().getMessage());
        }

        @Test
        void shouldPropagateExceptionWhenPasswordEncodingFails() {
            when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
            when(userPersistencePort.getUserByEmail(any(Email.class))).thenReturn(null);
            when(passwordEncoder.encode("admin123"))
                    .thenThrow(new RuntimeException("Password encoding failed"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dataInitializer.run());

            assertEquals("Fallo en la inicialización de datos", exception.getMessage());
            assertEquals("Password encoding failed", exception.getCause().getMessage());
        }
    }
}