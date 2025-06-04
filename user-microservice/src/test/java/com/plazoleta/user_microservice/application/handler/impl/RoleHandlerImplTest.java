package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.RoleRequestDto;
import com.plazoleta.user_microservice.application.dto.response.RoleResponseDto;
import com.plazoleta.user_microservice.application.mapper.IRoleRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IRoleResponseMapper;
import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleHandlerImplTest {

    @Mock
    private IRoleServicePort servicePort;

    @Mock
    private IRoleRequestMapper requestMapper;

    @Mock
    private IRoleResponseMapper responseMapper;

    @InjectMocks
    private RoleHandlerImpl handler;

    @Test
    void createRole_ShouldCallSaveRole() {
        RoleRequestDto requestDto = new RoleRequestDto();
        requestDto.setName("ROLE_ADMIN");
        requestDto.setDescription("Administrador");
        Role role = new Role(1L,RoleList.ROLE_ADMIN,"Administrador");

        when(requestMapper.toRole(requestDto)).thenReturn(role);

        handler.createRole(requestDto);

        verify(servicePort).saveRole(role);
    }

    @Test
    void getAllRoles_ShouldReturnMappedResponse() {
        Role role = new Role(1L,RoleList.ROLE_ADMIN,"Administrator");
        List<Role> roles = List.of(role);
        RoleResponseDto responseDto = new RoleResponseDto();
        responseDto.setName("ROLE_ADMIN");
        responseDto.setDescription("Administrador");

        when(servicePort.getAllRoles()).thenReturn(roles);
        when(responseMapper.toRoleResponseDtoList(roles)).thenReturn(List.of(responseDto));

        List<RoleResponseDto> result = handler.getAllRoles();

        assertEquals(1, result.size());
        assertEquals("ROLE_ADMIN", result.get(0).getName());
    }

    @Test
    void getRoleById_ShouldReturnMappedRole() {
        Role role = new Role(1L,RoleList.ROLE_CUSTOMER,"Cliente");
        RoleResponseDto responseDto = new RoleResponseDto();
        responseDto.setName("ROLE_CUSTOMER");
        responseDto.setDescription("Cliente");

        when(servicePort.getRole(1L)).thenReturn(role);
        when(responseMapper.toRoleResponseDto(role)).thenReturn(responseDto);

        RoleResponseDto result = handler.getRoleById(1L);

        assertEquals("ROLE_CUSTOMER", result.getName());
    }

    @Test
    void getRoleByName_ShouldReturnMappedRole() {
        Role role = new Role(1L,RoleList.ROLE_EMPLOYED,"Empleado");
        RoleResponseDto responseDto = new RoleResponseDto();
        responseDto.setName("ROLE_EMPLOYED");
        responseDto.setDescription("Empleado");

        when(servicePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(role);
        when(responseMapper.toRoleResponseDto(role)).thenReturn(responseDto);

        RoleResponseDto result = handler.getRoleByName(RoleList.ROLE_EMPLOYED);

        assertEquals("ROLE_EMPLOYED", result.getName());
    }


    @Test
    void updateRole_shouldCallServiceWithMappedRole() {
        Long id = 3L;
        RoleRequestDto requestDto = new RoleRequestDto();
        requestDto.setName("ROLE_CUSTOMER");
        requestDto.setDescription("ROLE_CUSTOMER");
        Role role = new Role(id, RoleList.ROLE_CUSTOMER,"ROLE_CUSTOMER");

        when(requestMapper.toRole(requestDto)).thenReturn(role);

        handler.updateRole(id, requestDto);

        verify(servicePort).updateRole(id, role);
    }

    @Test
    void deleteRole_shouldCallServiceDelete() {
        Long id = 4L;

        handler.deleteRole(id);

        verify(servicePort).deleteRole(id);
    }
}
