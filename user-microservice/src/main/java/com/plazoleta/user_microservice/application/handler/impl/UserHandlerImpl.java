package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.CreateBasicUserRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.user_microservice.application.handler.IUserHandler;
import com.plazoleta.user_microservice.application.mapper.ICreateBasicUserMapper;
import com.plazoleta.user_microservice.application.mapper.ICreateEmployedRequest;
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
    private final ICreateEmployedRequest iCreateEmployedRequest;
    private final ICreateBasicUserMapper iCreateBasicUserMapper;
    private final IUserResponseMapper iUserResponseMapper;
    private final IPasswordEncoderPort passwordEncoder;
    private final IAuthenticatedUserHandler authenticatedUserHandler;

    @Override
    public void createOwner(CreateOwnerRequestDto createOwnerRequestDto) {
        User user = iUserRequestMapper.toUser(createOwnerRequestDto);

        processUserCreation(user);
    }

    @Override
    public void createEmployed(CreateEmployedRequestDto createEmployedRequestDto) {
        User user = iCreateEmployedRequest.toUser(createEmployedRequestDto);

        processUserCreation(user);
    }

    @Override
    public void createCustomer(CreateBasicUserRequestDto createBasicUserRequestDto) {
        User user = iCreateBasicUserMapper.toUser(createBasicUserRequestDto);
        Role newUserRole = iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER);
        user.setRole(newUserRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        iUserServicePort.saveUser(user, newUserRole);
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
    public void updateUser(Long id, CreateOwnerRequestDto createOwnerRequestDto) {
        iUserServicePort.getUser(id);

        User userToUpdate = iUserRequestMapper.toUser(createOwnerRequestDto);

        String updaterRole = authenticatedUserHandler.getCurrentUserRole()
                .orElseThrow(() -> new IllegalArgumentException("No authenticated user role found."));

        Role role = iRoleServicePort.getRoleByName(RoleList.valueOf(updaterRole));
        userToUpdate.setId(id);
        userToUpdate.setRole(resolveRoleToAssign(role).orElseThrow(() -> new IllegalArgumentException("The current role is not allowed to create users.")));

        String passwordEncode = passwordEncoder.encode(createOwnerRequestDto.getPassword());
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

    private void processUserCreation(User user) {
        String currentUserRole = authenticatedUserHandler.getCurrentUserRole()
                .filter(role -> !role.equals("ROLE_ANONYMOUS"))
                .orElse("ROLE_CUSTOMER");
        Role creatorRole = iRoleServicePort.getRoleByName(RoleList.valueOf(currentUserRole));
        Role newUserRole = resolveRoleToAssign(creatorRole)
                .orElseThrow(() -> new IllegalArgumentException("The current role is not allowed to create users."));
        user.setRole(newUserRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        iUserServicePort.saveUser(user, creatorRole);
    }

    public Optional<Role> resolveRoleToAssign(Role creatorRole) {
        if (creatorRole.isAdmin()) {
            return Optional.ofNullable(iRoleServicePort.getRoleByName(RoleList.ROLE_OWNER));
        }
        if (creatorRole.isOwner()) {
            return Optional.ofNullable(iRoleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED));
        }
        if (creatorRole.isEmployed() || creatorRole.isCustomer()){
            return Optional.ofNullable(iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER));
        }
        return Optional.empty();
    }
}