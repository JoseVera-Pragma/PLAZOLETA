package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidUserRoleException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserSecurityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserSecurityPort userSecurityPort;

    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;
    private User validOwner;

    @BeforeEach
    void setup() {
        validOwner = new User.Builder()
                .role("ROLE_OWNER")
                .build();

        validRestaurant = new Restaurant.Builder()
                .idOwner(1L)
                .name("Valid Restaurant")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .build();

        restaurantUseCase = new RestaurantUseCase(restaurantPersistencePort, userSecurityPort);
    }

    @Test
    void createRestaurantShouldThrowWhenUserNotFound() {
        when(userSecurityPort.getUserById(validRestaurant.getIdOwner())).thenReturn(null);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                restaurantUseCase.createRestaurant(validRestaurant));

        assertEquals("User not found", ex.getMessage());
        verifyNoInteractions(restaurantPersistencePort);
    }

    @Test
    void createRestaurantShouldThrowWhenUserRoleIsInvalid() {
        User nonOwnerUser = new User.Builder()
                .role("ROLE_CUSTOMER")
                .build();

        when(userSecurityPort.getUserById(validRestaurant.getIdOwner())).thenReturn(nonOwnerUser);

        InvalidUserRoleException ex = assertThrows(InvalidUserRoleException.class, () ->
                restaurantUseCase.createRestaurant(validRestaurant));

        assertEquals("User does not have the required role", ex.getMessage());
        verifyNoInteractions(restaurantPersistencePort);
    }

    @Test
    void createRestaurantShouldThrowWhenNitExists() {
        when(userSecurityPort.getUserById(validRestaurant.getIdOwner())).thenReturn(validOwner);
        when(restaurantPersistencePort.existsByNit(validRestaurant.getNit())).thenReturn(true);

        DuplicateNitException ex = assertThrows(DuplicateNitException.class, () ->
                restaurantUseCase.createRestaurant(validRestaurant));

        assertEquals("A restaurant with the NIT '123456' already exists.", ex.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void createRestaurantShouldSaveWhenValid() {
        when(userSecurityPort.getUserById(validRestaurant.getIdOwner())).thenReturn(validOwner);
        when(restaurantPersistencePort.existsByNit(validRestaurant.getNit())).thenReturn(false);

        assertDoesNotThrow(() -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, times(1)).saveRestaurant(validRestaurant);
    }

    @Test
    void testFindAllRestaurantsWithPagination() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

        Restaurant r1 = new Restaurant.Builder()
                .id(1L)
                .name("A")
                .urlLogo("Logo 1")
                .nit("1321231321")
                .phoneNumber("31215454")
                .address("calle 1 2d2d")
                .idOwner(1L)
                .build();

        Restaurant r2 = new Restaurant.Builder()
                .id(2L)
                .name("B")
                .urlLogo("Logo 2")
                .nit("1321231321")
                .phoneNumber("31215454")
                .address("calle 1 2d2d")
                .idOwner(1L)
                .build();

        List<Restaurant> restaurantList = List.of(r1, r2);
        Page<Restaurant> expectedPage = new PageImpl<>(restaurantList, pageable, restaurantList.size());

        when(restaurantPersistencePort.findAll(pageable)).thenReturn(expectedPage);

        Page<Restaurant> result = restaurantUseCase.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("A", result.getContent().getFirst().getName());
        verify(restaurantPersistencePort).findAll(pageable);
    }
}