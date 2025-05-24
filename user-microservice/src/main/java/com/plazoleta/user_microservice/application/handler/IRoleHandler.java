package com.plazoleta.user_microservice.application.handler;

import com.plazoleta.user_microservice.application.dto.request.RoleRequestDto;
import com.plazoleta.user_microservice.application.dto.response.RoleResponseDto;
import com.plazoleta.user_microservice.domain.model.RoleList;

import java.util.List;

public interface IRoleHandler {
    void createRole(RoleRequestDto roleRequestDto);
    List<RoleResponseDto> getAllRoles();
    RoleResponseDto getRoleById(Long id);
    RoleResponseDto getRoleByName(RoleList name);
    void updateRole(Long id, RoleRequestDto roleRequestDto);
    void deleteRole(Long id);
}
