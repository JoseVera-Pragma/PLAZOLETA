package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.RoleRequestDto;
import com.plazoleta.user_microservice.application.dto.response.RoleResponseDto;
import com.plazoleta.user_microservice.application.handler.IRoleHandler;
import com.plazoleta.user_microservice.application.mapper.IRoleRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IRoleResponseMapper;
import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleHandlerImpl implements IRoleHandler {

    private final IRoleServicePort iRoleServicePort;
    private final IRoleResponseMapper iRoleResponseMapper;
    private final IRoleRequestMapper iRoleRequestMapper;


    @Override
    public void createRole(RoleRequestDto roleRequestDto) {
        iRoleServicePort.saveRole(iRoleRequestMapper.toRole(roleRequestDto));
    }

    @Override
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roleList = iRoleServicePort.getAllRoles();

        if (roleList == null){
            throw new RoleNotFoundException("No roles found in the system.");
        }

        return iRoleResponseMapper.toRoleResponseDtoList(roleList);
    }

    @Override
    public RoleResponseDto getRoleById(Long id) {
        Role role = iRoleServicePort.getRole(id);
        return iRoleResponseMapper.toRoleResponseDto(role);
    }

    @Override
    public RoleResponseDto getRoleByName(RoleList name) {
        Role role = iRoleServicePort.getRoleByName(name);
        return iRoleResponseMapper.toRoleResponseDto(role);
    }

    @Override
    public void updateRole(Long id, RoleRequestDto roleRequestDto) {
        Role existingRole = iRoleServicePort.getRole(id);
        existingRole.setName(RoleList.valueOf(roleRequestDto.getName()));
        existingRole.setDescription(roleRequestDto.getDescription());
        iRoleServicePort.updateRole(existingRole);
    }

    @Override
    public void deleteRole(Long id) {
        iRoleServicePort.getRole(id);
        iRoleServicePort.deleteRole(id);
    }
}
