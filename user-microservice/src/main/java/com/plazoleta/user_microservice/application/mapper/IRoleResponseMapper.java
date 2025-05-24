package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.response.RoleResponseDto;
import com.plazoleta.user_microservice.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRoleResponseMapper {

    RoleResponseDto toRoleResponseDto(Role role);

    List<RoleResponseDto> toRoleResponseDtoList(List<Role> roleList);

}
