package com.plazoleta.user_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.user_microservice.domain.exception.RoleAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.infrastructure.exception.NotDataFoundException;
import com.plazoleta.user_microservice.infrastructure.exception.RoleAssignedException;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleJpaAdapterTest {

    @InjectMocks
    private RoleJpaAdapter roleJpaAdapter;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleEntityMapper roleMapper;

    private Role role;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        role = new Role(1L, RoleList.ROLE_ADMIN,"Admin del sistema");
        roleEntity = new RoleEntity(1L, RoleList.ROLE_ADMIN, "Admin del sistema");
    }

    @Test
    void saveRole_ShouldSaveSuccessfully_WhenRoleDoesNotExist() {
        when(roleRepository.findByName(RoleList.ROLE_ADMIN)).thenReturn(null);
        when(roleMapper.toRoleEntity(role)).thenReturn(roleEntity);
        when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
        when(roleMapper.toRole(roleEntity)).thenReturn(role);

        Role saved = roleJpaAdapter.saveRole(role);

        assertEquals(role.getName(), saved.getName());
        verify(roleRepository).save(roleEntity);
    }

    @Test
    void saveRole_ShouldThrowException_WhenRoleExists() {
        when(roleRepository.findByName(RoleList.ROLE_ADMIN)).thenReturn(roleEntity);

        assertThrows(RoleAlreadyExistsException.class, () -> roleJpaAdapter.saveRole(role));
    }

    @Test
    void getRole_ShouldReturnRole_WhenFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
        when(roleMapper.toRole(roleEntity)).thenReturn(role);

        Role result = roleJpaAdapter.getRole(1L);

        assertNotNull(result);
        assertEquals(RoleList.ROLE_ADMIN, result.getName());
    }

    @Test
    void getRole_ShouldThrowException_WhenNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleJpaAdapter.getRole(1L));
    }

    @Test
    void getRoleByName_ShouldReturnRole() {
        when(roleRepository.findByName(RoleList.ROLE_ADMIN)).thenReturn(roleEntity);
        when(roleMapper.toRole(roleEntity)).thenReturn(role);

        Role result = roleJpaAdapter.getRoleByName(RoleList.ROLE_ADMIN);

        assertNotNull(result);
        assertEquals(RoleList.ROLE_ADMIN, result.getName());
    }

    @Test
    void getAllRole_ShouldReturnRoleList_WhenDataExists() {
        List<RoleEntity> entityList = List.of(roleEntity);
        List<Role> domainList = List.of(role);

        when(roleRepository.findAll()).thenReturn(entityList);
        when(roleMapper.toRoleList(entityList)).thenReturn(domainList);

        List<Role> result = roleJpaAdapter.getAllRole();

        assertEquals(1, result.size());
        assertEquals(RoleList.ROLE_ADMIN, result.get(0).getName());
    }

    @Test
    void getAllRole_ShouldThrowException_WhenNoData() {
        when(roleRepository.findAll()).thenReturn(List.of());

        assertThrows(NotDataFoundException.class, () -> roleJpaAdapter.getAllRole());
    }

    @Test
    void updateRole_ShouldUpdate_WhenExists() {
        when(roleRepository.existsById(role.getId())).thenReturn(true);
        when(roleMapper.toRoleEntity(role)).thenReturn(roleEntity);

        roleJpaAdapter.updateRole(role);

        verify(roleRepository).save(roleEntity);
    }

    @Test
    void updateRole_ShouldThrow_WhenNotFound() {
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        assertThrows(RoleNotFoundException.class, () -> roleJpaAdapter.updateRole(role));
    }

    @Test
    void deleteRole_ShouldDelete_WhenExistsAndNotAssigned() {
        when(roleRepository.existsById(role.getId())).thenReturn(true);
        when(userRepository.existsByRoleId(role.getId())).thenReturn(false);

        roleJpaAdapter.deleRole(role.getId());

        verify(roleRepository).deleteById(role.getId());
    }

    @Test
    void deleteRole_ShouldThrow_WhenNotExists() {
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        assertThrows(RoleNotFoundException.class, () -> roleJpaAdapter.deleRole(role.getId()));
    }

    @Test
    void deleteRole_ShouldThrow_WhenRoleIsAssigned() {
        when(roleRepository.existsById(role.getId())).thenReturn(true);
        when(userRepository.existsByRoleId(role.getId())).thenReturn(true);

        assertThrows(RoleAssignedException.class, () -> roleJpaAdapter.deleRole(role.getId()));
    }

    @Test
    void isRoleAssignedToUsers_ShouldReturnTrue_IfExists() {
        when(userRepository.existsByRoleId(1L)).thenReturn(true);
        assertTrue(roleJpaAdapter.isRoleAssignedToUsers(1L));
    }

    @Test
    void isRoleAssignedToUsers_ShouldReturnFalse_IfNotExists() {
        when(userRepository.existsByRoleId(1L)).thenReturn(false);
        assertFalse(roleJpaAdapter.isRoleAssignedToUsers(1L));
    }
}
