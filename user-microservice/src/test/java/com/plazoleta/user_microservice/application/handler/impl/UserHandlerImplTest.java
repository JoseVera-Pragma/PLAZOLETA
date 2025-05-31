package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.dto.request.CreateBasicUserRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.user_microservice.application.mapper.ICreateBasicUserMapper;
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
    private ICreateEmployedRequest iCreateEmployedRequest;

    @Mock
    private ICreateBasicUserMapper createBasicUserMapper;

    @Mock
    private IUserResponseMapper userResponseMapper;

    @Mock
    private IPasswordEncoderPort passwordEncoder;

    @Mock
    private IAuthenticatedUserHandler authenticatedUserHandler;

    @InjectMocks
    private UserHandlerImpl userHandler;

    private CreateOwnerRequestDto requestDto;
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
        requestDto = new CreateOwnerRequestDto();
        requestDto.setFirstName("First");
        requestDto.setLastName("Last");
        requestDto.setIdentityNumber("1231321322");
        requestDto.setPhoneNumber("+573111551451");
        requestDto.setDateOfBirth(LocalDate.of(2007,1,2));
        requestDto.setEmail("owner@test.com");
        requestDto.setPassword("password");
    }


    @Test
    void testCreateOwner_WhenAdminCreates_ShouldAssignOwnerRole() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(adminRole);
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$abcdef");

        userHandler.createOwner(requestDto);

        assertEquals(ownerRole, user.getRole());
        verify(userServicePort).saveUser(user, adminRole);
    }

    @Test
    void testCreateOwner_WhenRoleNotAllowed_ShouldThrowException() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.of("ROLE_ADMIN"));
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(ownerRole);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userHandler.createOwner(requestDto));
        assertEquals("The current role is not allowed to create users.", thrown.getMessage());
    }

    @Test
    void testCreateOwner_WhenOtherRolesCreates_ShouldThrowsException() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn("ROLE_ADMIN".describeConstable());
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_ADMIN)).thenReturn(ownerRole);

        assertThrows(IllegalArgumentException.class,()-> userHandler.createOwner(requestDto));
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

    @Test
    void testCreateOwner_WhenNoRoleAvailable_ShouldAssignDefaultCustomerRole() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.empty());
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(new Role(5L, RoleList.ROLE_CUSTOMER, "Cliente"));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        userHandler.createOwner(requestDto);

        assertEquals(RoleList.ROLE_CUSTOMER, user.getRole().getName());
    }

    @Test
    void testCreateOwner_WhenOwnerCreates_ShouldAssignEmployedRole() {
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.of("ROLE_OWNER"));
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);

        Role employedRole = new Role(3L, RoleList.ROLE_EMPLOYED, "Empleado");
        when(roleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(employedRole);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$abcdef");

        userHandler.createOwner(requestDto);

        assertEquals(employedRole, user.getRole());
        verify(userServicePort).saveUser(user, ownerRole);
    }

    @Test
    void testCreateOwner_WhenEmployedCreates_ShouldAssignCustomerRole() {
        Role employedRole = new Role(3L, RoleList.ROLE_EMPLOYED, "Empleado");
        Role customerRole = new Role(4L, RoleList.ROLE_CUSTOMER, "Cliente");

        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.of("ROLE_EMPLOYED"));
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(employedRole);
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRole);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$abcdef");

        userHandler.createOwner(requestDto);

        assertEquals(customerRole, user.getRole());
        verify(userServicePort).saveUser(user, employedRole);
    }

    @Test
    void testCreateOwner_WhenNoRoleAuthenticated_ShouldAssignCustomerRoleByDefault() {
        Role customerRole = new Role(4L, RoleList.ROLE_CUSTOMER, "Cliente");

        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.empty());
        when(userRequestMapper.toUser(requestDto)).thenReturn(user);
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRole);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$abcdef");

        userHandler.createOwner(requestDto);

        assertEquals(customerRole, user.getRole());
        verify(userServicePort).saveUser(user, customerRole); // Aquí el creatorRole es customer por defecto
    }

    @Test
    void testUpdateUser_NoAuthenticatedRole_ShouldThrowException() {
        Long userId = 1L;
        when(authenticatedUserHandler.getCurrentUserRole()).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userHandler.updateUser(userId, requestDto));

        assertEquals("No authenticated user role found.", exception.getMessage());
    }

    @Test
    void testResolveRoleToAssign_AdminRole_ReturnsOwnerRole() {
        Role adminRole = mock(Role.class);
        when(adminRole.isAdmin()).thenReturn(true);

        Role ownerRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(adminRole);

        assertTrue(result.isPresent());
        assertEquals(ownerRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_OwnerRole_ReturnsEmployedRole() {
        Role ownerRole = mock(Role.class);
        when(ownerRole.isAdmin()).thenReturn(false);
        when(ownerRole.isOwner()).thenReturn(true);

        Role employedRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(employedRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(ownerRole);

        assertTrue(result.isPresent());
        assertEquals(employedRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_EmployedRole_ReturnsCustomerRole() {
        Role employedRole = mock(Role.class);
        when(employedRole.isAdmin()).thenReturn(false);
        when(employedRole.isOwner()).thenReturn(false);
        when(employedRole.isEmployed()).thenReturn(true);

        Role customerRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(employedRole);

        assertTrue(result.isPresent());
        assertEquals(customerRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_CustomerRole_ReturnsCustomerRole() {
        Role customerRole = mock(Role.class);
        when(customerRole.isAdmin()).thenReturn(false);
        when(customerRole.isOwner()).thenReturn(false);
        when(customerRole.isEmployed()).thenReturn(false);
        when(customerRole.isCustomer()).thenReturn(true);

        Role customerRoleResult = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRoleResult);

        Optional<Role> result = userHandler.resolveRoleToAssign(customerRole);

        assertTrue(result.isPresent());
        assertEquals(customerRoleResult, result.get());
    }

    @Test
    void testResolveRoleToAssign_CustomerRoleOnly() {
        Role customerRole = mock(Role.class);
        when(customerRole.isAdmin()).thenReturn(false);
        when(customerRole.isOwner()).thenReturn(false);
        when(customerRole.isEmployed()).thenReturn(false);   // important
        when(customerRole.isCustomer()).thenReturn(true);

        Role customerRoleResult = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRoleResult);

        Optional<Role> result = userHandler.resolveRoleToAssign(customerRole);

        assertTrue(result.isPresent());
        assertEquals(customerRoleResult, result.get());
    }

    @Test
    void testResolveRoleToAssign_NoMatchingRole() {
        Role unknownRole = mock(Role.class);
        when(unknownRole.isAdmin()).thenReturn(false);
        when(unknownRole.isOwner()).thenReturn(false);
        when(unknownRole.isEmployed()).thenReturn(false);
        when(unknownRole.isCustomer()).thenReturn(false);

        Optional<Role> result = userHandler.resolveRoleToAssign(unknownRole);

        assertTrue(result.isEmpty());
    }

    @Test
    void testResolveRoleToAssign_Admin() {
        Role adminRole = mock(Role.class);
        when(adminRole.isAdmin()).thenReturn(true);

        Role ownerRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(ownerRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(adminRole);

        assertTrue(result.isPresent());
        assertEquals(ownerRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_Owner() {
        Role owner = mock(Role.class);
        when(owner.isAdmin()).thenReturn(false);
        when(owner.isOwner()).thenReturn(true);

        Role employedRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_EMPLOYED)).thenReturn(employedRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(owner);

        assertTrue(result.isPresent());
        assertEquals(employedRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_Employed() {
        Role employed = mock(Role.class);
        when(employed.isAdmin()).thenReturn(false);
        when(employed.isOwner()).thenReturn(false);
        when(employed.isEmployed()).thenReturn(true);

        Role customerRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(employed);

        assertTrue(result.isPresent());
        assertEquals(customerRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_Customer() {
        Role customer = mock(Role.class);
        when(customer.isAdmin()).thenReturn(false);
        when(customer.isOwner()).thenReturn(false);
        when(customer.isEmployed()).thenReturn(false);
        when(customer.isCustomer()).thenReturn(true);

        Role customerRole = new Role();
        when(roleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(customerRole);

        Optional<Role> result = userHandler.resolveRoleToAssign(customer);

        assertTrue(result.isPresent());
        assertEquals(customerRole, result.get());
    }

    @Test
    void testResolveRoleToAssign_NoMatch() {
        Role unknown = mock(Role.class);
        when(unknown.isAdmin()).thenReturn(false);
        when(unknown.isOwner()).thenReturn(false);
        when(unknown.isEmployed()).thenReturn(false);
        when(unknown.isCustomer()).thenReturn(false);

        Optional<Role> result = userHandler.resolveRoleToAssign(unknown);

        assertTrue(result.isEmpty());
    }

    @Test
    void testResolveRoleToAssign_AdminWithNullRole() {
        Role adminRole = mock(Role.class);
        when(adminRole.isAdmin()).thenReturn(true);

        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(null);

        Optional<Role> result = userHandler.resolveRoleToAssign(adminRole);

        assertTrue(result.isEmpty());
    }

    @Test
    void testResolveRoleToAssign_AdminRoleNull() {
        Role adminRole = mock(Role.class);
        when(adminRole.isAdmin()).thenReturn(true);
        when(roleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(null);

        Optional<Role> result = userHandler.resolveRoleToAssign(adminRole);

        assertTrue(result.isEmpty());
    }


}
