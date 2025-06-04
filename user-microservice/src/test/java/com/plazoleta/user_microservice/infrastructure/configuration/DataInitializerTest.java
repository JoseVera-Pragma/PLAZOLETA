package com.plazoleta.user_microservice.infrastructure.configuration;

import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class DataInitializerTest {

    private IRolePersistencePort rolePersistencePort;
    private IUserPersistencePort userPersistencePort;
    private IPasswordEncoderPort passwordEncoder;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        rolePersistencePort = mock(IRolePersistencePort.class);
        userPersistencePort = mock(IUserPersistencePort.class);
        passwordEncoder = mock(IPasswordEncoderPort.class);
        dataInitializer = new DataInitializer(rolePersistencePort, userPersistencePort, passwordEncoder);
    }

    @Test
    void shouldInitializeRolesAndAdminUserWhenNoneExist() {
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Role(1L, RoleList.ROLE_ADMIN, "Administrador del sistema")));
        when(userPersistencePort.getUserByEmail("admin@plazoleta.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        dataInitializer.run();

        verify(rolePersistencePort, atLeastOnce()).saveRole(any(Role.class));
        verify(userPersistencePort).saveUser(any(User.class));
    }

    @Test
    void shouldSkipRoleCreationIfRolesExist() {
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN))
                .thenReturn(Optional.of(new Role(1L, RoleList.ROLE_ADMIN, "Administrador del sistema")));

        dataInitializer.run();

        verify(rolePersistencePort, never()).saveRole(any(Role.class));
    }

    @Test
    void shouldNotCreateAdminUserIfExists() {
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN))
                .thenReturn(Optional.of(new Role(1L, RoleList.ROLE_ADMIN, "Administrador del sistema")));
        when(userPersistencePort.getUserByEmail("admin@plazoleta.com"))
                .thenReturn(Optional.of(User.builder().email("admin@plazoleta.com").build()));

        dataInitializer.run();

        verify(userPersistencePort, never()).saveUser(any(User.class));
    }
}