package com.plazoleta.user_microservice.application.handler;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateUserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;

import java.util.List;

public interface IUserHandler {

    void createOwner(CreateOwnerRequestDto createOwnerRequestDto);

    void createEmployed(CreateEmployedRequestDto createEmployedRequestDto);

    void createCustomer(CreateUserRequestDto createUserRequestDto);

    UserResponseDto getUser(Long id);

    UserResponseDto getUserByEmail(String email);

    List<UserResponseDto> getAllUsers();

    void updateUser(Long id, CreateUserRequestDto createUserRequestDto);

    void deleteUser(Long id);

}
