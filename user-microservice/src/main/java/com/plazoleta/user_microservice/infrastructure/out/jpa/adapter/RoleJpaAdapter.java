package com.plazoleta.user_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.user_microservice.domain.exception.RoleAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.infrastructure.exception.NotDataFoundException;
import com.plazoleta.user_microservice.infrastructure.exception.RoleAssignedException;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleJpaAdapter implements IRolePersistencePort {

    private final IRoleRepository iRoleRepository;
    private final IUserRepository iUserRepository;
    private final IRoleEntityMapper iRoleEntityMapper;

    @Override
    public Role saveRole(Role role) {
        if (iRoleRepository.findByName(role.getName()) != null) {
            throw new RoleAlreadyExistsException("Role already exists with name: " + role.getName().name());
        }
        RoleEntity entity = iRoleEntityMapper.toRoleEntity(role);
        RoleEntity save = iRoleRepository.save(entity);
        return iRoleEntityMapper.toRole(save);
    }

    @Override
    public Role getRole(Long id) {
        RoleEntity entity = iRoleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + id));;
        return iRoleEntityMapper.toRole(entity);
    }

    @Override
    public Role getRoleByName(RoleList name) {
        RoleEntity entity = iRoleRepository.findByName(name);
        return iRoleEntityMapper.toRole(entity);
    }

    @Override
    public List<Role> getAllRole() {
        List<RoleEntity> roleEntityList = iRoleRepository.findAll();
        if (roleEntityList.isEmpty()) {
            throw new NotDataFoundException("No roles found in the system.");
        }
        return iRoleEntityMapper.toRoleList(roleEntityList);
    }

    @Override
    public void updateRole(Role role) {
        if (!iRoleRepository.existsById(role.getId())) {
            throw new RoleNotFoundException("Cannot update: Role not found with ID: " + role.getId());
        }
        RoleEntity entity = iRoleEntityMapper.toRoleEntity(role);
        iRoleRepository.save(entity);
    }

    @Override
    public void deleRole(Long id) {
        if (!iRoleRepository.existsById(id)) {
            throw new RoleNotFoundException("Cannot delete: Role not found with ID: " + id);
        }
        if (isRoleAssignedToUsers(id)) {
            throw new RoleAssignedException();
        }
        iRoleRepository.deleteById(id);
    }

    @Override
    public boolean isRoleAssignedToUsers(Long roleId) {
        return iUserRepository.existsByRoleId(roleId);
    }
}
