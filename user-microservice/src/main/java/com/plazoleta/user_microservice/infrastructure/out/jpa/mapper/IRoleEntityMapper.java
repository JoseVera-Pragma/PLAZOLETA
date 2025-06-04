package com.plazoleta.user_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRoleEntityMapper {

    Role toRole(RoleEntity roleEntity);

    RoleEntity toRoleEntity(Role role);

    List<Role> toRoleList(List<RoleEntity> roleEntityList);
}
