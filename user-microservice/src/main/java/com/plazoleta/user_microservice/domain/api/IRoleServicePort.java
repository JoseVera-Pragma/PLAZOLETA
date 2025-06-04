package com.plazoleta.user_microservice.domain.api;

import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;

import java.util.List;

public interface IRoleServicePort {
    Role saveRole(Role role);

    Role getRole(Long id);

    Role getRoleByName(RoleList name);

    List<Role> getAllRoles();

    void updateRole(Long id, Role role);

    void deleteRole(Long id);
}
