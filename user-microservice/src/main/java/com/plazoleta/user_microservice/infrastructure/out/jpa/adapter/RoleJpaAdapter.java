package com.plazoleta.user_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleJpaAdapter implements IRolePersistencePort {

    private final IRoleRepository iRoleRepository;
    private final IUserRepository iUserRepository;
    private final IRoleEntityMapper iRoleEntityMapper;

    @Override
    public Role saveRole(Role role) {
        RoleEntity entity = iRoleEntityMapper.toRoleEntity(role);
        RoleEntity save = iRoleRepository.save(entity);
        return iRoleEntityMapper.toRole(save);
    }

    @Override
    public Optional<Role> getRole(Long id) {
        return iRoleRepository.findById(id)
                .map(iRoleEntityMapper::toRole);
    }

    @Override
    public Optional<Role> getRoleByName(RoleList name) {
        return iRoleRepository.findByName(name)
                .map(iRoleEntityMapper::toRole);
    }

    @Override
    public List<Role> getAllRole() {
        List<RoleEntity> roleEntityList = iRoleRepository.findAll();
        return iRoleEntityMapper.toRoleList(roleEntityList);
    }

    @Override
    public void updateRole(Role role) {
        RoleEntity entity = iRoleEntityMapper.toRoleEntity(role);
        iRoleRepository.save(entity);
    }

    @Override
    public void deleRole(Long id) {
        iRoleRepository.deleteById(id);
    }

    @Override
    public boolean isRoleAssignedToUsers(Long roleId) {
        return iUserRepository.existsByRoleId(roleId);
    }
}
