package com.plazoleta.user_microservice.application.handler;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;

import java.util.List;

public interface IUserHandler {

    void createUser(UserRequestDto userRequestDto);

    void createEmployed(CreateEmployedRequestDto createEmployedRequestDto);

    UserResponseDto getUser(Long id);

    UserResponseDto getUserByEmail(String email);

    List<UserResponseDto> getAllUsers();

    void updateUser(Long id, UserRequestDto userRequestDto);

    void deleteUser(Long id);

}
