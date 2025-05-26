package com.plazoleta.user_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    @InjectMocks
    private UserJpaAdapter userJpaAdapter;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IUserEntityMapper userEntityMapper;

    private User user;
    private UserEntity userEntity;
    private IdentityNumber identityNumber;
    private PhoneNumber phoneNumber;
    private Email email;

    @BeforeEach
    void setUp() {
        identityNumber = new IdentityNumber("12231321321");
        phoneNumber = new PhoneNumber("+3151651519");
        email = new Email("test@email.com");
        Role role = new Role(1L, RoleList.ROLE_ADMIN, "Administrador");

        user = User.builder()
                .id(1L)
                .firstName("First")
                .lastName("Last")
                .identityNumber(identityNumber)
                .dateOfBirth(LocalDate.of(2007, 1, 1))
                .phoneNumber(phoneNumber)
                .email(email)
                .password("password")
                .role(role)
                .build();

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("First");
        userEntity.setLastName("Last");
        userEntity.setIdentityNumber("12231321321");
        userEntity.setDateOfBirth(LocalDate.of(2007, 1, 1));
        userEntity.setPhoneNumber("+3151651519");
        userEntity.setEmail(email.getValue());
        userEntity.setPassword("password");

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(RoleList.ROLE_ADMIN);
        roleEntity.setDescription("Administrador");
        userEntity.setRole(roleEntity);
    }

    @Test
    void saveUser_ShouldSaveSuccessfully() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userEntityMapper.toUserEntity(user)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userEntityMapper.toUser(userEntity)).thenReturn(user);

        User result = userJpaAdapter.saveUser(user);

        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).save(userEntity);
    }

    @Test
    void saveUser_ShouldThrowUserAlreadyExistsException() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userJpaAdapter.saveUser(user));
    }

    @Test
    void getUser_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityMapper.toUser(userEntity)).thenReturn(user);

        User result = userJpaAdapter.getUser(1L);

        assertNotNull(result);
    }

    @Test
    void getUser_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userJpaAdapter.getUser(1L));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(userEntity);
        when(userEntityMapper.toUser(userEntity)).thenReturn(user);

        User result = userJpaAdapter.getUserByEmail(new Email("test@email.com"));

        assertNotNull(result);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<UserEntity> entityList = Collections.singletonList(userEntity);
        List<User> domainList = Collections.singletonList(user);

        when(userRepository.findAll()).thenReturn(entityList);
        when(userEntityMapper.toUserList(entityList)).thenReturn(domainList);

        List<User> result = userJpaAdapter.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userEntityMapper.toUserEntity(user)).thenReturn(userEntity);

        userJpaAdapter.updateUser(user);

        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUser_ShouldThrowUserNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userJpaAdapter.updateUser(user));
    }

    @Test
    void deleteUser_ShouldDeleteSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userJpaAdapter.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowUserNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userJpaAdapter.deleteUser(1L));
    }
}