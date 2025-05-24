package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IUserHandler;
import com.plazoleta.user_microservice.application.mapper.IUserRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IUserResponseMapper;
import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandlerImpl implements IUserHandler {

    private final IUserServicePort iUserServicePort;
    private final IRoleServicePort iRoleServicePort;
    private final IUserRequestMapper iUserRequestMapper;
    private final IUserResponseMapper iUserResponseMapper;

    @Override
    public void createUser(UserRequestDto userRequestDto) {
        User user = iUserRequestMapper.toUser(userRequestDto);
        Role creatorRole = iRoleServicePort.getRoleByName(RoleList.ROLE_ADMIN);
        Role newUserRole = resolveRoleToAssign(creatorRole);
        user.setRole(newUserRole);
        iUserServicePort.saveUser(user, creatorRole);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        User user = iUserServicePort.getUser(id);
        return iUserResponseMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = iUserServicePort.getUserByEmail(iUserRequestMapper.toEmail(email));
        return iUserResponseMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = iUserServicePort.getAllUsers();
        return iUserResponseMapper.toResponseDtoList(users);
    }

    @Override
    public void updateUser(Long id, UserRequestDto userRequestDto) {
        iUserServicePort.getUser(id);

        User userToUpdate = iUserRequestMapper.toUser(userRequestDto);

        Role creatorRole = iRoleServicePort.getRoleByName(RoleList.ROLE_ADMIN);

        userToUpdate.setId(id);
        userToUpdate.setRole(resolveRoleToAssign(creatorRole));
        iUserServicePort.updateUser(userToUpdate, creatorRole);
    }

    @Override
    public void deleteUser(Long id) {
        iUserServicePort.getUser(id);
        iUserServicePort.deleteUser(id);
    }



    private Role resolveRoleToAssign(Role creatorRole) {
        if (creatorRole.isAdmin()) {
            return iRoleServicePort.getRoleByName(RoleList.ROLE_OWNER);
        } else if (creatorRole.isOwner()) {
            return iRoleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED);
        } else {
            return iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER);
        }
    }

}
