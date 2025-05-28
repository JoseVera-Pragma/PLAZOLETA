package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.model.User;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_shouldReturnUser_whenFeignClientReturnsUser() {
        Long userId = 1L;
        User expectedUser = new User.Builder()
                .firstName("string")
                .lastName("string")
                .identityNumber("82992378008930303382364348441465148555688249808251")
                .phoneNumber("7699624734559")
                .dateOfBirth("2007-05-20")
                .email("string@string.test")
                .password("82992378008930303382364348441465148555688249808251")
                .role("1")
                .build();

        when(userFeignClient.getUserById(userId)).thenReturn(expectedUser);

        User actualUser = userFeignAdapter.getUserById(userId);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());

        verify(userFeignClient, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_shouldReturnNull_whenFeignClientReturnsNull() {
        Long userId = 2L;
        when(userFeignClient.getUserById(userId)).thenReturn(null);

        User actualUser = userFeignAdapter.getUserById(userId);

        assertNull(actualUser);
        verify(userFeignClient, times(1)).getUserById(userId);
    }
}
