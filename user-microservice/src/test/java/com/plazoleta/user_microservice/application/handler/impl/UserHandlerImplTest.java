package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateUserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.mapper.ICreateOwnerRequestMapper;
import com.plazoleta.user_microservice.application.mapper.ICreateUserMapper;
import com.plazoleta.user_microservice.application.mapper.ICreateEmployedRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IUserResponseMapper;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.model.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHandlerImplTest {

    private IUserServicePort userServicePort;
    private ICreateUserMapper createUserMapper;
    private ICreateEmployedRequestMapper createEmployedRequestMapper;
    private ICreateOwnerRequestMapper createOwnerRequestMapper;
    private IUserResponseMapper userResponseMapper;
    private UserHandlerImpl userHandler;
    private User defaultUserOwner;
    private User defaultUserEmployed;
    private User defaultUserCustomer;


    @BeforeEach
    void setUp() {
        userServicePort = mock(IUserServicePort.class);
        createUserMapper = mock(ICreateUserMapper.class);
        createEmployedRequestMapper = mock(ICreateEmployedRequestMapper.class);
        createOwnerRequestMapper = mock(ICreateOwnerRequestMapper.class);
        userResponseMapper = mock(IUserResponseMapper.class);
        userHandler = new UserHandlerImpl(userServicePort, createUserMapper, createEmployedRequestMapper, createOwnerRequestMapper, userResponseMapper);
        defaultUserOwner = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("pass123")
                .dateOfBirth(LocalDate.of(1988, 3, 14))
                .identityNumber("456123789")
                .phoneNumber("3124567890")
                .role(new Role(2L, RoleList.ROLE_OWNER,"ROLE_OWNER"))
                .build();

        defaultUserEmployed = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("pass123")
                .dateOfBirth(LocalDate.of(1988, 3, 14))
                .identityNumber("456123789")
                .phoneNumber("3124567890")
                .role(new Role(2L, RoleList.ROLE_EMPLOYED,"ROLE_EMPLOYED"))
                .restaurantId(5L)
                .build();

        defaultUserCustomer = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("test@example.com")
                .password("pass123")
                .dateOfBirth(LocalDate.of(1988, 3, 14))
                .identityNumber("456123789")
                .phoneNumber("3124567890")
                .role(new Role(2L, RoleList.ROLE_CUSTOMER,"ROLE_CUSTOMER"))
                .build();
    }

    @Test
    void createOwner_shouldMapAndCallService() {
        CreateOwnerRequestDto dto = new CreateOwnerRequestDto();
        User user = defaultUserOwner;

        when(createOwnerRequestMapper.toUser(dto)).thenReturn(user);

        userHandler.createOwner(dto);

        verify(userServicePort).saveOwnerUser(user);
    }

    @Test
    void createEmployed_shouldMapAndCallService() {
        CreateEmployedRequestDto dto = new CreateEmployedRequestDto();
        User user = defaultUserEmployed;

        when(createEmployedRequestMapper.toUser(dto)).thenReturn(user);

        userHandler.createEmployed(dto);

        verify(userServicePort).saveEmployedUser(user);
    }

    @Test
    void createCustomer_shouldMapAndCallService() {
        CreateUserRequestDto dto = new CreateUserRequestDto();
        User user = defaultUserCustomer;

        when(createUserMapper.toUser(dto)).thenReturn(user);

        userHandler.createCustomer(dto);

        verify(userServicePort).saveCustomerUser(user);
    }

    @Test
    void getUser_shouldReturnMappedUser() {
        Long id = 1L;
        User user = defaultUserCustomer;
        UserResponseDto dto = new UserResponseDto();

        when(userServicePort.getUser(id)).thenReturn(user);
        when(userResponseMapper.toUserResponseDto(user)).thenReturn(dto);

        UserResponseDto result = userHandler.getUser(id);

        assertEquals(dto, result);
    }

    @Test
    void getUserByEmail_shouldReturnMappedUser() {
        String email = "test@example.com";
        User user = defaultUserCustomer;
        UserResponseDto dto = new UserResponseDto();

        when(userServicePort.getUserByEmail(email)).thenReturn(user);
        when(userResponseMapper.toUserResponseDto(user)).thenReturn(dto);

        UserResponseDto result = userHandler.getUserByEmail(email);

        assertEquals(dto, result);
    }

    @Test
    void getAllUsers_shouldReturnMappedList() {
        List<User> users = List.of(defaultUserCustomer, defaultUserEmployed);
        List<UserResponseDto> dtos = List.of(new UserResponseDto(), new UserResponseDto());

        when(userServicePort.getAllUsers()).thenReturn(users);
        when(userResponseMapper.toResponseDtoList(users)).thenReturn(dtos);

        List<UserResponseDto> result = userHandler.getAllUsers();

        assertEquals(dtos, result);
    }

    @Test
    void updateUser_shouldMapAndCallService() {
        Long id = 1L;
        CreateUserRequestDto dto = new CreateUserRequestDto();
        User user = defaultUserCustomer;

        when(createUserMapper.toUser(dto)).thenReturn(user);

        userHandler.updateUser(id, dto);

        verify(userServicePort).updateUser(id, user);
    }

    @Test
    void deleteUser_shouldCallService() {
        Long id = 1L;

        userHandler.deleteUser(id);

        verify(userServicePort).deleteUser(id);
    }
}