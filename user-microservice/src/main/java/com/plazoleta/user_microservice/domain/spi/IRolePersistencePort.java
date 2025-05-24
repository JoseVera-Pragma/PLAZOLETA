package com.plazoleta.user_microservice.domain.spi;

import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;

import java.util.List;
import java.util.Optional;

public interface IRolePersistencePort {

    Role saveRole(Role role);

    Optional<Role> getRole(Long id);

    Optional<Role> getRoleByName(RoleList name);

    List<Role> getAllRole();

    void updateRole(Role role);

    void deleRole(Long id);
}
