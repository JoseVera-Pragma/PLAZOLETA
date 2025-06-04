package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateUserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.mapper.ICreateOwnerRequestMapper;
import com.plazoleta.user_microservice.application.mapper.ICreateUserMapper;
import com.plazoleta.user_microservice.application.handler.IUserHandler;
import com.plazoleta.user_microservice.application.mapper.ICreateEmployedRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IUserResponseMapper;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandlerImpl implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final ICreateUserMapper createUserMapper;
    private final ICreateEmployedRequestMapper iCreateEmployedRequest;
    private final ICreateOwnerRequestMapper createOwnerRequestMapper;
    private final IUserResponseMapper iUserResponseMapper;

    @Override
    public void createOwner(CreateOwnerRequestDto createOwnerRequestDto) {
        User user = createOwnerRequestMapper.toUser(createOwnerRequestDto);
        userServicePort.saveOwnerUser(user);
    }

    @Override
    public void createEmployed(CreateEmployedRequestDto createEmployedRequestDto) {
        User user = iCreateEmployedRequest.toUser(createEmployedRequestDto);
        userServicePort.saveEmployedUser(user);
    }

    @Override
    public void createCustomer(CreateUserRequestDto createUserRequestDto) {
        User user = createUserMapper.toUser(createUserRequestDto);
        userServicePort.saveCustomerUser(user);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        User user = userServicePort.getUser(id);
        return iUserResponseMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userServicePort.getUserByEmail(email);
        return iUserResponseMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userServicePort.getAllUsers();
        return iUserResponseMapper.toResponseDtoList(users);
    }

    @Override
    public void updateUser(Long id, CreateUserRequestDto createUserRequestDto) {
        User userToUpdate = createUserMapper.toUser(createUserRequestDto);
        userServicePort.updateUser(id, userToUpdate);
    }

    @Override
    public void deleteUser(Long id) {
        userServicePort.deleteUser(id);
    }
}