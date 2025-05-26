package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.exception.RoleAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.RoleInUseException;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseTest {

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @InjectMocks
    private RoleUseCase roleUseCase;

    private final Role sampleRole = new Role(1L, RoleList.ROLE_ADMIN,"Administrador");

    @Test
    void shouldSaveRoleSuccessfullyWhenNotExists() {
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(null);
        when(rolePersistencePort.saveRole(sampleRole)).thenReturn(sampleRole);

        Role result = roleUseCase.saveRole(sampleRole);

        assertEquals(sampleRole, result);
        verify(rolePersistencePort).saveRole(sampleRole);
    }

    @Test
    void shouldThrowWhenRoleAlreadyExistsOnSave() {
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(sampleRole);
        assertThrows(RoleAlreadyExistsException.class, () -> roleUseCase.saveRole(sampleRole));
    }

    @Test
    void shouldReturnRoleById() {
        when(rolePersistencePort.getRole(1L)).thenReturn(sampleRole);

        Role result = roleUseCase.getRole(1L);

        assertEquals(sampleRole, result);
    }

    @Test
    void shouldReturnRoleByName() {
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(sampleRole);

        Role result = roleUseCase.getRoleByName(RoleList.ROLE_ADMIN);

        assertEquals(sampleRole, result);
    }

    @Test
    void shouldReturnAllRoles() {
        List<Role> roles = List.of(sampleRole);

        when(rolePersistencePort.getAllRole()).thenReturn(roles);

        List<Role> result = roleUseCase.getAllRoles();

        assertEquals(roles, result);
    }

    @Test
    void shouldUpdateRoleSuccessfully() {
        Role updatedRole = new Role(1L, RoleList.ROLE_OWNER,"Propietario");

        when(rolePersistencePort.getRole(1L)).thenReturn(sampleRole);
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(null);

        roleUseCase.updateRole(updatedRole);

        verify(rolePersistencePort).updateRole(updatedRole);
    }

    @Test
    void shouldUpdateRoleDescriptionSuccessfully() {
        Role updatedRole = new Role(1L, RoleList.ROLE_ADMIN,"Propietario");

        when(rolePersistencePort.getRole(1L)).thenReturn(sampleRole);
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(sampleRole);

        roleUseCase.updateRole(updatedRole);

        verify(rolePersistencePort).updateRole(updatedRole);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentRole() {
        when(rolePersistencePort.getRole(1L)).thenReturn(null);

        assertThrows(RoleNotFoundException.class, () -> roleUseCase.updateRole(sampleRole));
    }

    @Test
    void shouldThrowWhenUpdatingRolNameAlreadyInUse() {
        Role otherRole = new Role(2L, RoleList.ROLE_OWNER,"Propietario");
        when(rolePersistencePort.getRole(1L)).thenReturn(sampleRole);
        when(rolePersistencePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(otherRole);

        Role updatedRole = new Role(1L, RoleList.ROLE_OWNER,"Propietario");

        assertThrows(RoleAlreadyExistsException.class, () -> roleUseCase.updateRole(updatedRole));
    }

    @Test
    void shouldDeleteRoleSuccessfully() {
        when(rolePersistencePort.getRole(1L)).thenReturn(sampleRole);
        when(rolePersistencePort.isRoleAssignedToUsers(1L)).thenReturn(false);

        roleUseCase.deleteRole(1L);

        verify(rolePersistencePort).deleRole(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistenceRole() {
        when(rolePersistencePort.getRole(1L)).thenReturn(null);

        assertThrows(RoleNotFoundException.class, () -> roleUseCase.deleteRole(1L));
    }

    @Test
    void shouldThrowWhenDeletingRolAssignedToUsers() {
        when(rolePersistencePort.getRole(1L)).thenReturn(sampleRole);
        when(rolePersistencePort.isRoleAssignedToUsers(1L)).thenReturn(true);

        assertThrows(RoleInUseException.class, () -> roleUseCase.deleteRole(1L));
    }
}
