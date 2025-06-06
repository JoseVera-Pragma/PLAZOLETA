package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.user_microservice.application.handler.IUserHandler;
import com.plazoleta.user_microservice.application.mapper.IUserRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IUserResponseMapper;
import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandlerImpl implements IUserHandler {

    private final IUserServicePort iUserServicePort;
    private final IRoleServicePort iRoleServicePort;
    private final IUserRequestMapper iUserRequestMapper;
    private final IUserResponseMapper iUserResponseMapper;
    private final IPasswordEncoderPort passwordEncoder;
    private final IAuthenticatedUserHandler authenticatedUserHandler;

    @Override
    public void createUser(UserRequestDto userRequestDto) {
        User user = iUserRequestMapper.toUser(userRequestDto);

        String currentUserRole = authenticatedUserHandler.getCurrentUserRole()
                .orElseThrow(() -> new IllegalArgumentException("No authenticated user role found."));

        Role creatorRole = iRoleServicePort.getRoleByName(RoleList.valueOf(currentUserRole));
        Role newUserRole = resolveRoleToAssign(creatorRole).orElseThrow(() -> new IllegalArgumentException("The current role is not allowed to create users."));
        user.setRole(newUserRole);
        String passwordEncode = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(passwordEncode);
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

        String updaterRole = authenticatedUserHandler.getCurrentUserRole()
                .orElseThrow(() -> new IllegalArgumentException("No authenticated user role found."));

        Role role = iRoleServicePort.getRoleByName(RoleList.valueOf(updaterRole));
        userToUpdate.setId(id);
        userToUpdate.setRole(resolveRoleToAssign(role).orElseThrow(()-> new IllegalArgumentException("The current role is not allowed to create users.")));

        String passwordEncode = passwordEncoder.encode(userRequestDto.getPassword());
        userToUpdate.setPassword(passwordEncode);
        iUserServicePort.updateUser(userToUpdate, role);
    }

    @Override
    public void deleteUser(Long id) {
        iUserServicePort.getUser(id);

        String elimnatorRole = authenticatedUserHandler.getCurrentUserRole()
                .orElseThrow(() -> new IllegalArgumentException("No authenticated user role found."));

        Role roleEliminator = iRoleServicePort.getRoleByName(RoleList.valueOf(elimnatorRole));

        iUserServicePort.deleteUser(id, roleEliminator);
    }

    private Optional<Role> resolveRoleToAssign(Role creatorRole) {
        if (creatorRole.isAdmin()) {
            return Optional.ofNullable(iRoleServicePort.getRoleByName(RoleList.ROLE_OWNER));
        }
        if (creatorRole.isOwner()) {
            return Optional.ofNullable(iRoleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED));
        }

        return Optional.empty();
    }

}
