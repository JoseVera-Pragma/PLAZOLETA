package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.user_microservice.application.mapper.ICreateEmployedRequest;
import com.plazoleta.user_microservice.application.mapper.IUserRequestMapper;
import com.plazoleta.user_microservice.application.mapper.IUserResponseMapper;
import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.model.*;

import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserHandlerImplTest {

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IRoleServicePort roleServicePort;

    @Mock
    private IUserRequestMapper userRequestMapper;

    @Mock
    private IUserResponseMapper userResponseMapper;

    @Mock
    private IPasswordEncoderPort passwordEncoder;

    @Mock
    private IAuthenticatedUserHandler authenticatedUserHandler;

    @Mock
    private ICreateEmployedRequest iCreateEmployedRequest;

    @InjectMocks
    private UserHandlerImpl userHandler;

    private UserRequestDto requestDto;
    private User user;
    private Role adminRole;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role(1L, RoleList.ROLE_ADMIN,"Administrador");
        ownerRole = new Role(2L, RoleList.ROLE_OWNER,"Propietario");
        user = User.builder()
                .id(2L)
                .firstName("First")
                .lastName("Last")
                .identityNumber(new IdentityNumber("1231321322"))
                .phoneNumber(new PhoneNumber("+573111551451"))
                .dateOfBirth(LocalDate.of(2007, 1, 2))
                .email(new Email("owner@test.com"))
                .password("password")
                .role(ownerRole)
                .build();
        requestDto = new UserRequestDto();
        requestDto.setFirstName("First");
        requestDto.setLastName("Last");
        requestDto.setIdentityNumber("1231321322");
        requestDto.setPhoneNumber("+573111551451");
        requestDto.setDateOfBirth(LocalDate.of(2007,1,2));
        requestDto.setEmail("owner@test.com");
        requestDto.setPassword("password");
    }

    @Test
    void testCreateUser_WhenAdminCreates_ShouldAssignOwnerRole() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$abcdef");

        userHandler.createUser(requestDto);

        assertEquals(ownerRole, user.getRole());
        verify(userServicePort).saveUser(user, adminRole);
    }

    @Test
    void testCreateUser_WhenOtherRolesCreates_ShouldThrowsException() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(ownerRole);

        assertThrows(IllegalArgumentException.class,()-> userHandler.createUser(requestDto));
    }

    @Test
    void testCreateEmployed_WhenAdminCreates_ShouldAssignOwnerRole() {
        CreateEmployedRequestDto requestDto = new CreateEmployedRequestDto();
        requestDto.setFirstName("First");
        requestDto.setLastName("Last");
        requestDto.setIdentityNumber("1234567890");
        requestDto.setPhoneNumber("+573111111111");
        requestDto.setEmail("owner@example.com");
        requestDto.setPassword("plainPassword");

        User user = User.builder()
                .firstName("First")
                .lastName("Last")
                .identityNumber(new IdentityNumber("1234567890"))
                .phoneNumber(new PhoneNumber("+573111111111"))
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .email(new Email("owner@example.com"))
                .password("plainPassword")
                .build();

        Role adminRole = new Role(1L, RoleList.ROLE_ADMIN, "Administrador");
        Role ownerRole = new Role(2L, RoleList.ROLE_OWNER, "Propietario");

        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.of("ROLE_ADMIN"));
        when(iCreateEmployedRequest.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        userHandler.createEmployed(requestDto);

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(ownerRole, user.getRole());

        verify(userServicePort).saveUser(user, adminRole);
    }

    @Test
    void testGetUser_ShouldReturnResponseDto() {
        Long userId = 1L;
        when(userServicePort.getUser(userId)).thenReturn(user);
        UserResponseDto responseDto = new UserResponseDto();
        when(userResponseMapper.toUserResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userHandler.getUser(userId);

        assertEquals(responseDto, result);
    }

    @Test
    void testGetUserByEmail_ShouldReturnResponseDto() {
        String email = "user@example.com";
        Email formattedEmail = new Email(email);

        when(userRequestMapper.toEmail(email)).thenReturn(formattedEmail);
        when(userServicePort.getUserByEmail(formattedEmail)).thenReturn(user);
        UserResponseDto dto = new UserResponseDto();
        when(userResponseMapper.toUserResponseDto(user)).thenReturn(dto);

        UserResponseDto result = userHandler.getUserByEmail(email);

        assertEquals(dto, result);
    }

    @Test
    void testGetAllUsers_ShouldReturnResponseDtoList() {
        List<User> userList = List.of(user);
        List<UserResponseDto> dtoList = List.of(new UserResponseDto());

        when(userServicePort.getAllUsers()).thenReturn(userList);
        when(userResponseMapper.toResponseDtoList(userList)).thenReturn(dtoList);

        List<UserResponseDto> result = userHandler.getAllUsers();

        assertEquals(dtoList, result);
    }

    @Test
    void testUpdateUser_WhenOtherRolesUpdater_ShouldThrowsException() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        Long userId = 10L;

        when(userServicePort.getUser(userId)).thenReturn(user);
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(ownerRole);

        assertThrows(IllegalArgumentException.class, ()->userHandler.updateUser(userId, requestDto));
    }

    @Test
    void testUpdateUser_ShouldUpdateCorrectly() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        Long userId = 10L;

        when(userServicePort.getUser(userId)).thenReturn(user);
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);

        userHandler.updateUser(userId, requestDto);

        assertEquals(userId, user.getId());
        assertEquals(ownerRole, user.getRole());
        verify(userServicePort).updateUser(user, adminRole);
    }

    @Test
    void testDeleteUser_ShouldCallDelete() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        Long userId = 2L;
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);

        when(userServicePort.getUser(userId)).thenReturn(user);

        userHandler.deleteUser(userId);

        verify(userServicePort).deleteUser(userId,adminRole);
    }

    @Test
    void testCreateEmployed_WhenCreatorHasInvalidRole_ShouldThrowException() {
        CreateEmployedRequestDto createEmployedRequestDto = new CreateEmployedRequestDto();
        createEmployedRequestDto.setFirstName("First");
        createEmployedRequestDto.setLastName("Last");
        createEmployedRequestDto.setIdentityNumber("1231321322");
        createEmployedRequestDto.setPhoneNumber("+573111551451");
        createEmployedRequestDto.setEmail("owner@test.com");
        createEmployedRequestDto.setPassword("password");

        Role invalidRole = new Role(99L, RoleList.ROLE_EMPLOYED, "Empleado");
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_EMPLOYED".describeConstable());
        when(iCreateEmployedRequest.toUser(createEmployedRequestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(invalidRole);

        assertThrows(IllegalArgumentException.class, () -> userHandler.createEmployed(createEmployedRequestDto));
    }

}
