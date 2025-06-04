package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.exception.RoleAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.RoleInUseException;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;

import java.util.List;
import java.util.Optional;

public class RoleUseCase implements IRoleServicePort {

    private final IRolePersistencePort iRolePersistencePort;

    public RoleUseCase(IRolePersistencePort iRolePersistencePort) {
        this.iRolePersistencePort = iRolePersistencePort;
    }

    @Override
    public Role saveRole(Role role) {
        if (iRolePersistencePort.getRoleByName(role.getName()).isPresent()) {
            throw new RoleAlreadyExistsException("The role already exists: " + role.getName());
        }
        return iRolePersistencePort.saveRole(role);
    }

    @Override
    public Role getRole(Long id) {
        return iRolePersistencePort.getRole(id).orElseThrow(
                () -> new RoleNotFoundException("Role not found")
        );
    }

    @Override
    public Role getRoleByName(RoleList name) {
        return iRolePersistencePort.getRoleByName(name).orElseThrow(
                () -> new RoleNotFoundException("Role not found")
        );
    }

    @Override
    public List<Role> getAllRoles() {
        return iRolePersistencePort.getAllRole();
    }

    @Override
    public void updateRole(Long id, Role role) {
        if (iRolePersistencePort.getRole(id).isEmpty()) {
            throw new RoleNotFoundException("Cannot update: Role not found with ID: " + id);
        }

        Optional<Role> existingRoleByNameOpt = iRolePersistencePort.getRoleByName(role.getName());

        if (existingRoleByNameOpt.isPresent() && !existingRoleByNameOpt.get().getId().equals(id)) {
            throw new RoleAlreadyExistsException("A different role already uses the name: " + role.getName());
        }
        role.setId(id);
        iRolePersistencePort.updateRole(role);
    }

    @Override
    public void deleteRole(Long id) {
        if (iRolePersistencePort.getRole(id).isEmpty()) {
            throw new RoleNotFoundException("Cannot delete: Role not found with ID: " + id);
        }
        if (iRolePersistencePort.isRoleAssignedToUsers(id)) {
            throw new RoleInUseException("Cannot delete: Role is assigned to users.");
        }
        iRolePersistencePort.deleRole(id);
    }

}
