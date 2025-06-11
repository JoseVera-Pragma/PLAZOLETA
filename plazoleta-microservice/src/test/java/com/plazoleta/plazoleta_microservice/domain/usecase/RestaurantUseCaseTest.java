package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidUserRoleException;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserServiceClientPort;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IUserServiceClientPort userServiceClientPort;
    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    @Test
    void createRestaurant_shouldSave_whenValidOwnerAndUniqueNit() {

        String firstName = "Jose";
        String lastName = "Vera";
        String identityNumber = "1093854586";
        String phoneNumber = "3001234567";
        String dateOfBirth = "1990-01-01";
        String email = "jose@example.com";
        String password = "securePass123";
        String role = "ROLE_OWNER";
        Long restaurantId = 1L;

        User owner = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .identityNumber(identityNumber)
                .phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth)
                .email(email)
                .password(password)
                .role(role)
                .restaurantId(restaurantId)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .idOwner(1L)
                .nit("123456789")
                .name("Mi Restaurante")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .build();

        when(userServiceClientPort.findUserById(1L)).thenReturn(owner);
        when(restaurantPersistencePort.existsRestaurantByNit("123456789")).thenReturn(false);
        when(restaurantPersistencePort.saveRestaurant(restaurant)).thenReturn(restaurant);
        when(userServiceClientPort.findUserById(1L)).thenReturn(owner);

        Restaurant saved = restaurantUseCase.createRestaurant(restaurant);

        assertNotNull(saved);
        assertEquals("Mi Restaurante", saved.getName());
        verify(restaurantPersistencePort).saveRestaurant(restaurant);
    }

    @Test
    void createRestaurant_shouldThrowInvalidUserRole_whenUserIsNotOwner() {
        User user = User.builder()
                .role("ROLE_CUSTOMER")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .idOwner(2L)
                .nit("987654321")
                .build();

        when(userServiceClientPort.findUserById(2L)).thenReturn(user);

        InvalidUserRoleException exception = assertThrows(InvalidUserRoleException.class, () -> {
            restaurantUseCase.createRestaurant(restaurant);
        });

        assertEquals("User does not have the required role", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void createRestaurant_shouldThrowDuplicateNit_whenNitExists() {
        User owner = User.builder()
                .role("ROLE_OWNER")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .idOwner(3L)
                .nit("111222333")
                .build();

        when(userServiceClientPort.findUserById(3L)).thenReturn(owner);
        when(restaurantPersistencePort.existsRestaurantByNit("111222333")).thenReturn(true);

        DuplicateNitException exception = assertThrows(DuplicateNitException.class, () -> {
            restaurantUseCase.createRestaurant(restaurant);
        });

        assertTrue(exception.getMessage().contains("A restaurant with the NIT"));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void findAllRestaurants_shouldReturnList() {
        int pageIndex = 0;
        int pageSize = 2;

        List<Restaurant> restaurants = List.of(
                Restaurant.builder()
                        .id(1L)
                        .idOwner(1L)
                        .nit("123")
                        .name("Rest1")
                        .address("Address1")
                        .phoneNumber("Phone1")
                        .urlLogo("Img1")
                        .build(),

                Restaurant.builder()
                        .id(2L)
                        .idOwner(2L)
                        .nit("456")
                        .name("Rest2")
                        .address("Address2")
                        .phoneNumber("Phone2")
                        .urlLogo("Img2")
                        .build()
        );

        Page<Restaurant> expectedPage = new Page<>(restaurants, pageIndex, pageSize, 10L);

        when(restaurantPersistencePort.findAllRestaurants(pageIndex, pageSize)).thenReturn(expectedPage);

        Page<Restaurant> result = restaurantUseCase.findAllRestaurants(pageIndex, pageSize);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(10, result.getTotalElements());
        verify(restaurantPersistencePort, times(1)).findAllRestaurants(pageIndex, pageSize);
    }
}
