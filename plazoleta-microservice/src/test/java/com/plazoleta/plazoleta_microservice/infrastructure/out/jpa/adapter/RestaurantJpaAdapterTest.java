package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantJpaAdapterTest {

    @Mock
    private IRestaurantRepository restaurantRepository;

    @Mock
    private IRestaurantEntityMapper restaurantEntityMapper;

    @InjectMocks
    private RestaurantJpaAdapter restaurantJpaAdapter;

    private Restaurant domainRestaurant;
    private RestaurantEntity entityRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        domainRestaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante El Sabor")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("3201234567")
                .urlLogo("logo.png")
                .idOwner(99L)
                .build();

        entityRestaurant = new RestaurantEntity();
        entityRestaurant.setId(1L);
        entityRestaurant.setName("Restaurante El Sabor");
        entityRestaurant.setNit("123456789");
        entityRestaurant.setAddress("Calle 123");
        entityRestaurant.setPhoneNumber("3201234567");
        entityRestaurant.setUrlLogo("logo.png");
        entityRestaurant.setIdOwner(99L);
    }

    @Test
    void testSaveRestaurant() {
        when(restaurantEntityMapper.toRestaurantEntity(domainRestaurant)).thenReturn(entityRestaurant);
        when(restaurantRepository.save(entityRestaurant)).thenReturn(entityRestaurant);
        when(restaurantEntityMapper.toRestaurant(entityRestaurant)).thenReturn(domainRestaurant);

        Restaurant result = restaurantJpaAdapter.saveRestaurant(domainRestaurant);

        assertEquals(domainRestaurant, result);
        verify(restaurantRepository).save(entityRestaurant);
    }

    @Test
    void testExistsRestaurantByNitTrue() {
        when(restaurantRepository.existsByNit("123456789")).thenReturn(true);
        assertTrue(restaurantJpaAdapter.existsRestaurantByNit("123456789"));
    }

    @Test
    void testExistsRestaurantByNitFalse() {
        when(restaurantRepository.existsByNit("123456789")).thenReturn(false);
        assertFalse(restaurantJpaAdapter.existsRestaurantByNit("123456789"));
    }

    @Test
    void testFindRestaurantByIdFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(entityRestaurant));
        when(restaurantEntityMapper.toRestaurant(entityRestaurant)).thenReturn(domainRestaurant);

        Optional<Restaurant> result = restaurantJpaAdapter.findRestaurantById(1L);

        assertTrue(result.isPresent());
        assertEquals(domainRestaurant, result.get());
    }

    @Test
    void testFindRestaurantByIdNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Restaurant> result = restaurantJpaAdapter.findRestaurantById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllRestaurants() {
        List<RestaurantEntity> entities = List.of(entityRestaurant);
        Page<RestaurantEntity> page = new PageImpl<>(entities);

        when(restaurantRepository.findAll(PageRequest.of(0, 2))).thenReturn(page);
        when(restaurantEntityMapper.toRestaurant(entityRestaurant)).thenReturn(domainRestaurant);

        List<Restaurant> result = restaurantJpaAdapter.findAllRestaurants(0, 2);

        assertEquals(1, result.size());
        assertEquals(domainRestaurant, result.getFirst());
    }
}