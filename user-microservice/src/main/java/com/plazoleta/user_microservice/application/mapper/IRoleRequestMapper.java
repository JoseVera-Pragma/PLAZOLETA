package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.request.RoleRequestDto;
import com.plazoleta.user_microservice.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRoleRequestMapper {
    Role toRole(RoleRequestDto roleRequestDto);

    RoleRequestDto toRoleRequestDto(Role role);
}
