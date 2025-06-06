package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.UserServiceUnavailableException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserFeignAdapterTest {

    @Mock
    private IUserFeignClient userFeignClient;

    @InjectMocks
    private UserFeignAdapter userFeignAdapter;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .firstName("Jose")
                .lastName("Vera")
                .email("jose@mail.com")
                .phoneNumber("3210001111")
                .password("secure")
                .build();
    }

    @Test
    void testFindUserById_success() {
        when(userFeignClient.getUserById(1L)).thenReturn(user);

        User result = userFeignAdapter.findUserById(1L);

        assertEquals(user, result);
        verify(userFeignClient).getUserById(1L);
    }

    @Test
    void testFindUserById_serviceUnavailable() {
        FeignException feignException = mock(FeignException.class);
        when(userFeignClient.getUserById(1L)).thenThrow(feignException);

        UserServiceUnavailableException exception = assertThrows(
                UserServiceUnavailableException.class,
                () -> userFeignAdapter.findUserById(1L)
        );

        assertEquals("User service is currently unavailable", exception.getMessage());
        assertEquals(feignException, exception.getCause());
    }
}